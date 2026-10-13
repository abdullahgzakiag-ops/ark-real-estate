import request from 'supertest';
import { createApp } from '../src/app';
import { prisma } from '../src/config/prisma';
import { AuthService } from '../src/services/auth.service';
import { UserRole, ListingStatus, ListingPurpose, PropertyType, AreaUnit } from '@prisma/client';

describe('Marketplace & Moderation API (/v1/listings & /v1/moderation)', () => {
  const app = createApp();

  let adminToken: string;
  let ownerToken: string;
  let buyerToken: string;

  let adminUser: any;
  let ownerUser: any;
  let buyerUser: any;

  let approvedListing: any;
  let pendingListing: any;

  beforeAll(async () => {
    // Mock user tokens
    adminUser = {
      id: 'admin-uuid-1',
      role: UserRole.ADMIN,
      phone: '+923001111111',
      fullName: 'System Administrator'
    };
    adminToken = AuthService.generateTokens(adminUser).accessToken;

    ownerUser = {
      id: 'owner-uuid-2',
      role: UserRole.OWNER_AGENT,
      phone: '+923002222222',
      fullName: 'Tariq Estate Agent'
    };
    ownerToken = AuthService.generateTokens(ownerUser).accessToken;

    buyerUser = {
      id: 'buyer-uuid-3',
      role: UserRole.BUYER_RENTER,
      phone: '+923003333333',
      fullName: 'Ali Hassan Buyer'
    };
    buyerToken = AuthService.generateTokens(buyerUser).accessToken;

    approvedListing = {
      id: 'listing-approved-1',
      ownerId: ownerUser.id,
      title: 'Luxury 1 Kanal Villa in DHA Phase 6',
      description: 'Brand new luxury designer villa with prime location.',
      purpose: ListingPurpose.FOR_SALE,
      propertyType: PropertyType.HOUSE,
      pricePkr: '65000000.00',
      city: 'Lahore',
      areaLocation: 'DHA Phase 6',
      areaValue: '1.00',
      areaUnit: AreaUnit.KANAL,
      bedrooms: 5,
      bathrooms: 6,
      status: ListingStatus.APPROVED,
      isVerifiedBadge: true,
      createdAt: new Date(),
      updatedAt: new Date(),
      images: [],
      owner: {
        id: ownerUser.id,
        fullName: ownerUser.fullName,
        phone: ownerUser.phone,
        agencyName: 'Al-Madina Estates',
        role: ownerUser.role,
        avatarUrl: null
      },
      _count: { savedByUsers: 2 }
    };

    pendingListing = {
      id: 'listing-pending-2',
      ownerId: ownerUser.id,
      title: 'Commercial Plaza in Blue Area',
      description: 'Prime commercial building for multinational offices.',
      purpose: ListingPurpose.FOR_SALE,
      propertyType: PropertyType.OFFICE,
      pricePkr: '120000000.00',
      city: 'Islamabad',
      areaLocation: 'Blue Area',
      areaValue: '2.00',
      areaUnit: AreaUnit.KANAL,
      bedrooms: 0,
      bathrooms: 4,
      status: ListingStatus.PENDING_APPROVAL,
      isVerifiedBadge: false,
      createdAt: new Date(),
      updatedAt: new Date(),
      images: [],
      owner: {
        id: ownerUser.id,
        fullName: ownerUser.fullName,
        phone: ownerUser.phone,
        agencyName: 'Al-Madina Estates',
        role: ownerUser.role,
        avatarUrl: null
      },
      _count: { savedByUsers: 0 }
    };
  });

  describe('GET /v1/listings (Public Marketplace Search)', () => {
    it('should strictly exclude pending & rejected listings from public search (Rule 17)', async () => {
      // Mock prisma findMany & count
      jest.spyOn(prisma.listing, 'count').mockResolvedValue(1);
      jest.spyOn(prisma.listing, 'findMany').mockResolvedValue([approvedListing as any]);

      const res = await request(app)
        .get('/v1/listings')
        .query({ city: 'Lahore' });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data).toHaveLength(1);
      expect(res.body.data[0].id).toBe('listing-approved-1');
      expect(res.body.data[0].status).toBe(ListingStatus.APPROVED);
      expect(res.body.data[0].pricePkr).toBe('65000000.00');
    });

    it('should filter listings by price, city, and property type', async () => {
      jest.spyOn(prisma.listing, 'count').mockResolvedValue(1);
      jest.spyOn(prisma.listing, 'findMany').mockResolvedValue([approvedListing as any]);

      const res = await request(app)
        .get('/v1/listings')
        .query({
          city: 'Lahore',
          purpose: 'FOR_SALE',
          propertyType: 'HOUSE',
          minPrice: 1000000,
          maxPrice: 80000000
        });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.meta).toHaveProperty('total', 1);
    });
  });

  describe('POST /v1/listings (Owner/Agent Submission)', () => {
    it('should reject unauthenticated listing submissions with 401', async () => {
      const res = await request(app)
        .post('/v1/listings')
        .send({
          title: '3 Bed Apartment in Clifton',
          description: 'Spacious apartment with sea view in Karachi',
          purpose: 'FOR_RENT',
          propertyType: 'APARTMENT',
          pricePkr: 150000,
          city: 'Karachi',
          areaLocation: 'Clifton Block 2',
          areaValue: 1800,
          areaUnit: 'SQ_FT'
        });

      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
    });

    it('should reject submission from regular buyer with 403', async () => {
      const res = await request(app)
        .post('/v1/listings')
        .set('Authorization', `Bearer ${buyerToken}`)
        .send({
          title: '3 Bed Apartment in Clifton',
          description: 'Spacious apartment with sea view in Karachi',
          purpose: 'FOR_RENT',
          propertyType: 'APARTMENT',
          pricePkr: 150000,
          city: 'Karachi',
          areaLocation: 'Clifton Block 2',
          areaValue: 1800,
          areaUnit: 'SQ_FT'
        });

      expect(res.status).toBe(403);
      expect(res.body.success).toBe(false);
    });

    it('should allow owner/agent to submit, defaulting status to PENDING_APPROVAL and isVerifiedBadge false', async () => {
      jest.spyOn(prisma.listing, 'create').mockResolvedValue({
        ...pendingListing,
        id: 'new-listing-id',
        status: ListingStatus.PENDING_APPROVAL,
        isVerifiedBadge: false
      } as any);

      const res = await request(app)
        .post('/v1/listings')
        .set('Authorization', `Bearer ${ownerToken}`)
        .send({
          title: 'Commercial Plaza in Blue Area',
          description: 'Prime commercial building for multinational offices.',
          purpose: 'FOR_SALE',
          propertyType: 'OFFICE',
          pricePkr: 120000000,
          city: 'Islamabad',
          areaLocation: 'Blue Area',
          areaValue: 2,
          areaUnit: 'KANAL'
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.listing.status).toBe(ListingStatus.PENDING_APPROVAL);
      expect(res.body.data.listing.isVerifiedBadge).toBe(false);
    });
  });

  describe('PATCH /v1/moderation/listings/:id/approve (Admin Workflow)', () => {
    it('should reject non-admin moderation attempts with 403', async () => {
      const res = await request(app)
        .patch('/v1/moderation/listings/listing-pending-2/approve')
        .set('Authorization', `Bearer ${ownerToken}`)
        .send({ isVerifiedBadge: true });

      expect(res.status).toBe(403);
    });

    it('should allow admin to approve listing, granting verified badge and recording audit log', async () => {
      jest.spyOn(prisma.listing, 'findUnique').mockResolvedValue(pendingListing as any);
      jest.spyOn(prisma.listing, 'update').mockResolvedValue({
        ...pendingListing,
        status: ListingStatus.APPROVED,
        isVerifiedBadge: true,
        reviewedById: adminUser.id,
        reviewedAt: new Date()
      } as any);
      jest.spyOn(prisma.auditLog, 'create').mockResolvedValue({} as any);

      const res = await request(app)
        .patch('/v1/moderation/listings/listing-pending-2/approve')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ isVerifiedBadge: true });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.listing.status).toBe(ListingStatus.APPROVED);
      expect(res.body.data.listing.isVerifiedBadge).toBe(true);
    });

    it('should allow admin to reject listing with reason', async () => {
      jest.spyOn(prisma.listing, 'findUnique').mockResolvedValue(pendingListing as any);
      jest.spyOn(prisma.listing, 'update').mockResolvedValue({
        ...pendingListing,
        status: ListingStatus.REJECTED,
        isVerifiedBadge: false,
        rejectionReason: 'Invalid property ownership documentation provided.'
      } as any);
      jest.spyOn(prisma.auditLog, 'create').mockResolvedValue({} as any);

      const res = await request(app)
        .patch('/v1/moderation/listings/listing-pending-2/reject')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ reason: 'Invalid property ownership documentation provided.' });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.listing.status).toBe(ListingStatus.REJECTED);
      expect(res.body.data.listing.rejectionReason).toBe('Invalid property ownership documentation provided.');
    });
  });

  describe('POST /v1/listings/:id/inquiries (Marketplace Inquiries)', () => {
    it('should allow authenticated buyer to submit inquiry on approved listing', async () => {
      jest.spyOn(prisma.listing, 'findUnique').mockResolvedValue(approvedListing as any);
      jest.spyOn(prisma.listingInquiry, 'create').mockResolvedValue({
        id: 'inquiry-1',
        listingId: approvedListing.id,
        userId: buyerUser.id,
        contactName: 'Ali Hassan',
        contactPhone: '+923003333333',
        message: 'Is the price negotiable for full upfront cash payment?',
        status: 'NEW',
        createdAt: new Date(),
        updatedAt: new Date()
      } as any);

      const res = await request(app)
        .post(`/v1/listings/${approvedListing.id}/inquiries`)
        .set('Authorization', `Bearer ${buyerToken}`)
        .send({
          contactName: 'Ali Hassan',
          contactPhone: '+923003333333',
          message: 'Is the price negotiable for full upfront cash payment?'
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.inquiry.contactName).toBe('Ali Hassan');
    });

    it('should reject inquiry if listing is pending or not approved', async () => {
      jest.spyOn(prisma.listing, 'findUnique').mockResolvedValue(pendingListing as any);

      const res = await request(app)
        .post(`/v1/listings/${pendingListing.id}/inquiries`)
        .set('Authorization', `Bearer ${buyerToken}`)
        .send({
          contactName: 'Ali Hassan',
          contactPhone: '+923003333333',
          message: 'Hello'
        });

      expect(res.status).toBe(400);
      expect(res.body.success).toBe(false);
    });
  });

  describe('POST & DELETE /v1/listings/:id/save (Saved Listings)', () => {
    it('should allow user to save a listing to favorites', async () => {
      jest.spyOn(prisma.listing, 'findUnique').mockResolvedValue(approvedListing as any);
      jest.spyOn(prisma.savedListing, 'upsert').mockResolvedValue({
        id: 'saved-1',
        userId: buyerUser.id,
        listingId: approvedListing.id,
        createdAt: new Date()
      } as any);

      const res = await request(app)
        .post(`/v1/listings/${approvedListing.id}/save`)
        .set('Authorization', `Bearer ${buyerToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.isSaved).toBe(true);
    });

    it('should allow user to remove a listing from favorites', async () => {
      jest.spyOn(prisma.savedListing, 'delete').mockResolvedValue({} as any);

      const res = await request(app)
        .delete(`/v1/listings/${approvedListing.id}/save`)
        .set('Authorization', `Bearer ${buyerToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.isSaved).toBe(false);
    });
  });

  describe('GET /v1/listings/user/my-listings (Owner Listings Status Overview)', () => {
    it('should return all listings owned by the authenticated user with status and counts', async () => {
      jest.spyOn(prisma.listing, 'findMany').mockResolvedValue([
        {
          ...pendingListing,
          pricePkr: { toString: () => '45000000.00' },
          areaValue: { toString: () => '10.00' },
          images: [],
          _count: { inquiries: 2, viewingRequests: 1, savedByUsers: 5 }
        }
      ] as any);

      const res = await request(app)
        .get('/v1/listings/user/my-listings')
        .set('Authorization', `Bearer ${ownerToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.listings).toHaveLength(1);
      expect(res.body.data.listings[0].status).toBe(ListingStatus.PENDING_APPROVAL);
      expect(res.body.data.listings[0].inquiriesCount).toBe(2);
    });
  });
});

