import { Prisma, ListingStatus, ListingPurpose, PropertyType, AreaUnit, InquiryStatus } from '@prisma/client';
import { prisma } from '../config/prisma';
import { AppError } from '../utils/apiResponse';

export interface ListingFilterQuery {
  search?: string;
  city?: string;
  areaLocation?: string;
  purpose?: ListingPurpose;
  propertyType?: PropertyType;
  minPrice?: number;
  maxPrice?: number;
  bedrooms?: number;
  bathrooms?: number;
  isVerifiedBadge?: boolean;
  page?: number;
  limit?: number;
  sortBy?: 'newest' | 'price_asc' | 'price_desc';
}

export interface CreateListingInput {
  title: string;
  titleUrdu?: string;
  description: string;
  descriptionUrdu?: string;
  purpose: ListingPurpose;
  propertyType: PropertyType;
  pricePkr: number | string;
  isNegotiable?: boolean;
  city: string;
  areaLocation: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  areaValue: number | string;
  areaUnit: AreaUnit;
  bedrooms?: number;
  bathrooms?: number;
  carSpaces?: number;
  images?: Array<{
    storageKey: string;
    url: string;
    isFeatured?: boolean;
    sortOrder?: number;
  }>;
}

export interface UpdateListingInput extends Partial<CreateListingInput> {}

export class ListingService {
  /**
   * Public or authenticated search of marketplace listings.
   * Public searches MUST strictly filter status = APPROVED (Rule 17).
   */
  async getListings(filters: ListingFilterQuery, isAdminView: boolean = false) {
    const page = Math.max(1, Number(filters.page) || 1);
    const limit = Math.min(50, Math.max(1, Number(filters.limit) || 20));
    const skip = (page - 1) * limit;

    const where: Prisma.ListingWhereInput = {};

    // Enforce Rule 17: Non-admin public search excludes pending & rejected listings
    if (!isAdminView) {
      where.status = ListingStatus.APPROVED;
    }

    if (filters.city) {
      where.city = { equals: filters.city, mode: 'insensitive' };
    }

    if (filters.areaLocation) {
      where.areaLocation = { contains: filters.areaLocation, mode: 'insensitive' };
    }

    if (filters.purpose) {
      where.purpose = filters.purpose;
    }

    if (filters.propertyType) {
      where.propertyType = filters.propertyType;
    }

    if (filters.isVerifiedBadge !== undefined) {
      where.isVerifiedBadge = filters.isVerifiedBadge;
    }

    if (filters.bedrooms !== undefined) {
      where.bedrooms = { gte: Number(filters.bedrooms) };
    }

    if (filters.bathrooms !== undefined) {
      where.bathrooms = { gte: Number(filters.bathrooms) };
    }

    // Money precision filter in PKR
    if (filters.minPrice !== undefined || filters.maxPrice !== undefined) {
      where.pricePkr = {};
      if (filters.minPrice !== undefined) {
        where.pricePkr.gte = new Prisma.Decimal(filters.minPrice);
      }
      if (filters.maxPrice !== undefined) {
        where.pricePkr.lte = new Prisma.Decimal(filters.maxPrice);
      }
    }

    // General text search
    if (filters.search) {
      where.OR = [
        { title: { contains: filters.search, mode: 'insensitive' } },
        { description: { contains: filters.search, mode: 'insensitive' } },
        { areaLocation: { contains: filters.search, mode: 'insensitive' } },
        { city: { contains: filters.search, mode: 'insensitive' } }
      ];
    }

    // Sorting
    let orderBy: Prisma.ListingOrderByWithRelationInput = { createdAt: 'desc' };
    if (filters.sortBy === 'price_asc') {
      orderBy = { pricePkr: 'asc' };
    } else if (filters.sortBy === 'price_desc') {
      orderBy = { pricePkr: 'desc' };
    }

    const [total, listings] = await Promise.all([
      prisma.listing.count({ where }),
      prisma.listing.findMany({
        where,
        skip,
        take: limit,
        orderBy,
        include: {
          images: {
            orderBy: { sortOrder: 'asc' }
          },
          owner: {
            select: {
              id: true,
              fullName: true,
              phone: true,
              agencyName: true,
              role: true,
              avatarUrl: true
            }
          },
          _count: {
            select: { savedByUsers: true }
          }
        }
      })
    ]);

    return {
      listings: listings.map((l: any) => ({
        ...l,
        pricePkr: l.pricePkr.toString(),
        areaValue: l.areaValue.toString(),
        latitude: l.latitude ? l.latitude.toString() : null,
        longitude: l.longitude ? l.longitude.toString() : null,
        savedCount: l._count.savedByUsers
      })),
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    };
  }

  /**
   * Get single listing detail. Enforces moderation visibility rules.
   */
  async getListingById(id: string, requestingUser?: { id: string; role: string }) {
    const listing = await prisma.listing.findUnique({
      where: { id },
      include: {
        images: {
          orderBy: { sortOrder: 'asc' }
        },
        owner: {
          select: {
            id: true,
            fullName: true,
            phone: true,
            agencyName: true,
            role: true,
            avatarUrl: true
          }
        },
        _count: {
          select: { savedByUsers: true }
        }
      }
    });

    if (!listing) {
      throw new AppError('Listing not found', 404, 'NOT_FOUND');
    }

    // Non-approved listings can only be viewed by the listing owner or an admin
    if (listing.status !== ListingStatus.APPROVED) {
      const isOwner = requestingUser && requestingUser.id === listing.ownerId;
      const isAdmin = requestingUser && requestingUser.role === 'ADMIN';

      if (!isOwner && !isAdmin) {
        throw new AppError('Listing not found or pending administrative review', 404, 'NOT_FOUND');
      }
    }

    return {
      ...listing,
      pricePkr: listing.pricePkr.toString(),
      areaValue: listing.areaValue.toString(),
      latitude: listing.latitude ? listing.latitude.toString() : null,
      longitude: listing.longitude ? listing.longitude.toString() : null,
      savedCount: listing._count.savedByUsers
    };
  }

  /**
   * Submit new property listing.
   * Required status: PENDING_APPROVAL (Admin review mandatory).
   */
  async createListing(userId: string, data: CreateListingInput) {
    const listing = await prisma.listing.create({
      data: {
        ownerId: userId,
        title: data.title,
        titleUrdu: data.titleUrdu,
        description: data.description,
        descriptionUrdu: data.descriptionUrdu,
        purpose: data.purpose,
        propertyType: data.propertyType,
        pricePkr: new Prisma.Decimal(data.pricePkr),
        isNegotiable: data.isNegotiable ?? false,
        city: data.city,
        areaLocation: data.areaLocation,
        address: data.address,
        latitude: data.latitude !== undefined ? new Prisma.Decimal(data.latitude) : null,
        longitude: data.longitude !== undefined ? new Prisma.Decimal(data.longitude) : null,
        areaValue: new Prisma.Decimal(data.areaValue),
        areaUnit: data.areaUnit,
        bedrooms: data.bedrooms,
        bathrooms: data.bathrooms,
        carSpaces: data.carSpaces,
        status: ListingStatus.PENDING_APPROVAL,
        isVerifiedBadge: false, // Rule 19: Only admin approval can verify
        images: data.images?.length
          ? {
              create: data.images.map((img, idx) => ({
                storageKey: img.storageKey,
                url: img.url,
                isFeatured: img.isFeatured ?? idx === 0,
                sortOrder: img.sortOrder ?? idx
              }))
            }
          : undefined
      },
      include: {
        images: true,
        owner: {
          select: {
            id: true,
            fullName: true,
            phone: true,
            agencyName: true
          }
        }
      }
    });

    return {
      ...listing,
      pricePkr: listing.pricePkr.toString(),
      areaValue: listing.areaValue.toString()
    };
  }

  /**
   * Update listing. If owner updates, resets to PENDING_APPROVAL.
   */
  async updateListing(id: string, userId: string, userRole: string, data: UpdateListingInput) {
    const existing = await prisma.listing.findUnique({ where: { id } });
    if (!existing) {
      throw new AppError('Listing not found', 404, 'NOT_FOUND');
    }

    if (userRole !== 'ADMIN' && existing.ownerId !== userId) {
      throw new AppError('You do not have permission to modify this listing', 403, 'FORBIDDEN');
    }

    const updateData: Prisma.ListingUpdateInput = {};

    if (data.title) updateData.title = data.title;
    if (data.titleUrdu !== undefined) updateData.titleUrdu = data.titleUrdu;
    if (data.description) updateData.description = data.description;
    if (data.descriptionUrdu !== undefined) updateData.descriptionUrdu = data.descriptionUrdu;
    if (data.purpose) updateData.purpose = data.purpose;
    if (data.propertyType) updateData.propertyType = data.propertyType;
    if (data.pricePkr !== undefined) updateData.pricePkr = new Prisma.Decimal(data.pricePkr);
    if (data.isNegotiable !== undefined) updateData.isNegotiable = data.isNegotiable;
    if (data.city) updateData.city = data.city;
    if (data.areaLocation) updateData.areaLocation = data.areaLocation;
    if (data.address !== undefined) updateData.address = data.address;
    if (data.areaValue !== undefined) updateData.areaValue = new Prisma.Decimal(data.areaValue);
    if (data.areaUnit) updateData.areaUnit = data.areaUnit;
    if (data.bedrooms !== undefined) updateData.bedrooms = data.bedrooms;
    if (data.bathrooms !== undefined) updateData.bathrooms = data.bathrooms;
    if (data.carSpaces !== undefined) updateData.carSpaces = data.carSpaces;

    // If an owner edits an approved listing, reset to pending approval for re-moderation
    if (userRole !== 'ADMIN' && existing.status === ListingStatus.APPROVED) {
      updateData.status = ListingStatus.PENDING_APPROVAL;
      updateData.isVerifiedBadge = false;
    }

    const updated = await prisma.listing.update({
      where: { id },
      data: updateData,
      include: {
        images: true,
        owner: {
          select: {
            id: true,
            fullName: true,
            phone: true,
            agencyName: true
          }
        }
      }
    });

    return {
      ...updated,
      pricePkr: updated.pricePkr.toString(),
      areaValue: updated.areaValue.toString()
    };
  }

  /**
   * Delete listing.
   */
  async deleteListing(id: string, userId: string, userRole: string) {
    const existing = await prisma.listing.findUnique({ where: { id } });
    if (!existing) {
      throw new AppError('Listing not found', 404, 'NOT_FOUND');
    }

    if (userRole !== 'ADMIN' && existing.ownerId !== userId) {
      throw new AppError('You do not have permission to delete this listing', 403, 'FORBIDDEN');
    }

    await prisma.listing.delete({ where: { id } });
    return { success: true, message: 'Listing deleted successfully' };
  }

  /**
   * Admin Moderation: Approve or Reject listing with audit logging.
   */
  async moderateListing(
    id: string,
    adminId: string,
    action: 'APPROVE' | 'REJECT',
    reason?: string,
    isVerifiedBadge: boolean = true
  ) {
    const existing = await prisma.listing.findUnique({ where: { id } });
    if (!existing) {
      throw new AppError('Listing not found', 404, 'NOT_FOUND');
    }

    const isApprove = action === 'APPROVE';

    const updated = await prisma.listing.update({
      where: { id },
      data: {
        status: isApprove ? ListingStatus.APPROVED : ListingStatus.REJECTED,
        isVerifiedBadge: isApprove ? isVerifiedBadge : false,
        reviewedById: adminId,
        reviewedAt: new Date(),
        rejectionReason: isApprove ? null : (reason || 'Listing does not satisfy ARK moderation guidelines')
      },
      include: {
        owner: {
          select: {
            id: true,
            fullName: true,
            phone: true
          }
        }
      }
    });

    // Record audit trail
    await prisma.auditLog.create({
      data: {
        userId: adminId,
        action: isApprove ? 'LISTING_APPROVED' : 'LISTING_REJECTED',
        entityType: 'Listing',
        entityId: id,
        metadata: {
          action,
          reason: isApprove ? null : reason,
          previousStatus: existing.status,
          isVerifiedBadge: isApprove ? isVerifiedBadge : false
        }
      }
    });

    return {
      ...updated,
      pricePkr: updated.pricePkr.toString(),
      areaValue: updated.areaValue.toString()
    };
  }

  /**
   * Get pending listings queue for Admin dashboard.
   */
  async getPendingModerationQueue(page: number = 1, limit: number = 20) {
    const skip = (page - 1) * limit;

    const [total, listings] = await Promise.all([
      prisma.listing.count({ where: { status: ListingStatus.PENDING_APPROVAL } }),
      prisma.listing.findMany({
        where: { status: ListingStatus.PENDING_APPROVAL },
        skip,
        take: limit,
        orderBy: { createdAt: 'asc' },
        include: {
          images: true,
          owner: {
            select: {
              id: true,
              fullName: true,
              phone: true,
              agencyName: true
            }
          }
        }
      })
    ]);

    return {
      listings: listings.map((l: any) => ({
        ...l,
        pricePkr: l.pricePkr.toString(),
        areaValue: l.areaValue.toString()
      })),
      pagination: {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit)
      }
    };
  }

  /**
   * Submit buyer/renter inquiry for a listing.
   */
  async createInquiry(
    listingId: string,
    userId: string,
    data: { contactName: string; contactPhone: string; message: string }
  ) {
    const listing = await prisma.listing.findUnique({ where: { id: listingId } });
    if (!listing || listing.status !== ListingStatus.APPROVED) {
      throw new AppError('Cannot submit inquiry for inactive or unverified listing', 400, 'BAD_REQUEST');
    }

    return await prisma.listingInquiry.create({
      data: {
        listingId,
        userId,
        contactName: data.contactName,
        contactPhone: data.contactPhone,
        message: data.message,
        status: InquiryStatus.NEW
      }
    });
  }

  /**
   * Request on-site physical inspection / viewing.
   */
  async createViewingRequest(
    listingId: string,
    userId: string,
    data: {
      preferredDate: Date;
      preferredTime: string;
      contactPhone: string;
      notes?: string;
    }
  ) {
    const listing = await prisma.listing.findUnique({ where: { id: listingId } });
    if (!listing || listing.status !== ListingStatus.APPROVED) {
      throw new AppError('Cannot schedule inspection for inactive or unverified listing', 400, 'BAD_REQUEST');
    }

    return await prisma.viewingRequest.create({
      data: {
        listingId,
        userId,
        preferredDate: data.preferredDate,
        preferredTime: data.preferredTime,
        contactPhone: data.contactPhone,
        notes: data.notes,
        status: InquiryStatus.NEW
      }
    });
  }

  /**
   * Bookmark / save listing.
   */
  async saveListing(userId: string, listingId: string) {
    const listing = await prisma.listing.findUnique({ where: { id: listingId } });
    if (!listing) {
      throw new AppError('Listing not found', 404, 'NOT_FOUND');
    }

    return await prisma.savedListing.upsert({
      where: {
        userId_listingId: { userId, listingId }
      },
      create: { userId, listingId },
      update: {}
    });
  }

  /**
   * Unsave / remove bookmark.
   */
  async unsaveListing(userId: string, listingId: string) {
    try {
      await prisma.savedListing.delete({
        where: {
          userId_listingId: { userId, listingId }
        }
      });
      return { success: true };
    } catch {
      return { success: true }; // Idempotent
    }
  }

  /**
   * Get user's saved listings.
   */
  async getSavedListings(userId: string) {
    const saved = await prisma.savedListing.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      include: {
        listing: {
          include: {
            images: { orderBy: { sortOrder: 'asc' } },
            owner: {
              select: {
                id: true,
                fullName: true,
                phone: true,
                agencyName: true
              }
            }
          }
        }
      }
    });

    return saved.map((s: any) => ({
      savedAt: s.createdAt,
      listing: {
        ...s.listing,
        pricePkr: s.listing.pricePkr.toString(),
        areaValue: s.listing.areaValue.toString()
      }
    }));
  }

  /**
   * Get user's submitted listings (any status: PENDING_APPROVAL, APPROVED, REJECTED).
   * Powers Stitch 'my_listings_approval_status'.
   */
  async getMyListings(userId: string) {
    const listings = await prisma.listing.findMany({
      where: { ownerId: userId },
      orderBy: { createdAt: 'desc' },
      include: {
        images: { orderBy: { sortOrder: 'asc' } },
        _count: {
          select: {
            inquiries: true,
            viewingRequests: true,
            savedByUsers: true
          }
        }
      }
    });

    return listings.map((l: any) => ({
      ...l,
      pricePkr: l.pricePkr.toString(),
      areaValue: l.areaValue.toString(),
      latitude: l.latitude ? l.latitude.toString() : null,
      longitude: l.longitude ? l.longitude.toString() : null,
      inquiriesCount: l._count.inquiries,
      viewingRequestsCount: l._count.viewingRequests,
      savedCount: l._count.savedByUsers
    }));
  }
}

export const listingService = new ListingService();
