import { Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { listingService } from '../services/listing.service';
import { sendSuccess } from '../utils/apiResponse';
import { ListingPurpose, PropertyType, AreaUnit } from '@prisma/client';

export const listingFilterSchema = z.object({
  search: z.string().optional(),
  city: z.string().optional(),
  areaLocation: z.string().optional(),
  purpose: z.nativeEnum(ListingPurpose).optional(),
  propertyType: z.nativeEnum(PropertyType).optional(),
  minPrice: z.coerce.number().positive().optional(),
  maxPrice: z.coerce.number().positive().optional(),
  bedrooms: z.coerce.number().int().min(0).optional(),
  bathrooms: z.coerce.number().int().min(0).optional(),
  isVerifiedBadge: z.enum(['true', 'false']).transform(v => v === 'true').optional(),
  page: z.coerce.number().int().min(1).default(1),
  limit: z.coerce.number().int().min(1).max(50).default(20),
  sortBy: z.enum(['newest', 'price_asc', 'price_desc']).default('newest')
});

export const createListingSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters'),
  titleUrdu: z.string().optional(),
  description: z.string().min(10, 'Description must be at least 10 characters'),
  descriptionUrdu: z.string().optional(),
  purpose: z.nativeEnum(ListingPurpose),
  propertyType: z.nativeEnum(PropertyType),
  pricePkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  isNegotiable: z.boolean().optional().default(false),
  city: z.string().min(2, 'City is required'),
  areaLocation: z.string().min(2, 'Area/Society location is required'),
  address: z.string().optional(),
  latitude: z.number().optional(),
  longitude: z.number().optional(),
  areaValue: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  areaUnit: z.nativeEnum(AreaUnit),
  bedrooms: z.number().int().min(0).optional(),
  bathrooms: z.number().int().min(0).optional(),
  carSpaces: z.number().int().min(0).optional(),
  images: z.array(
    z.object({
      storageKey: z.string(),
      url: z.string().url(),
      isFeatured: z.boolean().optional(),
      sortOrder: z.number().int().optional()
    })
  ).optional()
});

export const updateListingSchema = createListingSchema.partial();

export const inquirySchema = z.object({
  contactName: z.string().min(2, 'Name must be at least 2 characters'),
  contactPhone: z.string().min(10, 'Phone must be valid Pakistani number'),
  message: z.string().min(5, 'Message must be at least 5 characters')
});

export const viewingRequestSchema = z.object({
  preferredDate: z.string().datetime({ message: 'Must be a valid ISO date' }),
  preferredTime: z.string().min(2, 'Time slot is required'),
  contactPhone: z.string().min(10, 'Phone must be valid Pakistani number'),
  notes: z.string().optional()
});

export class ListingController {
  async getListings(req: Request, res: Response, next: NextFunction) {
    try {
      const filters = listingFilterSchema.parse(req.query);
      const isAdminView = req.user?.role === 'ADMIN';
      const result = await listingService.getListings(filters, isAdminView);
      return sendSuccess(res, result.listings, 200, result.pagination);
    } catch (error) {
      next(error);
    }
  }

  async getListingById(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const listing = await listingService.getListingById(id, req.user);
      return sendSuccess(res, { listing }, 200);
    } catch (error) {
      next(error);
    }
  }

  async createListing(req: Request, res: Response, next: NextFunction) {
    try {
      const validated = createListingSchema.parse(req.body);
      const listing = await listingService.createListing(req.user!.id, validated);
      return sendSuccess(res, { listing }, 201);
    } catch (error) {
      next(error);
    }
  }

  async updateListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const validated = updateListingSchema.parse(req.body);
      const listing = await listingService.updateListing(id, req.user!.id, req.user!.role, validated);
      return sendSuccess(res, { listing }, 200);
    } catch (error) {
      next(error);
    }
  }

  async deleteListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const result = await listingService.deleteListing(id, req.user!.id, req.user!.role);
      return sendSuccess(res, result, 200);
    } catch (error) {
      next(error);
    }
  }

  async createInquiry(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const validated = inquirySchema.parse(req.body);
      const inquiry = await listingService.createInquiry(id, req.user!.id, validated);
      return sendSuccess(res, { inquiry }, 201);
    } catch (error) {
      next(error);
    }
  }

  async createViewingRequest(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const validated = viewingRequestSchema.parse(req.body);
      const viewing = await listingService.createViewingRequest(id, req.user!.id, {
        preferredDate: new Date(validated.preferredDate),
        preferredTime: validated.preferredTime,
        contactPhone: validated.contactPhone,
        notes: validated.notes
      });
      return sendSuccess(res, { viewing }, 201);
    } catch (error) {
      next(error);
    }
  }

  async saveListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      await listingService.saveListing(req.user!.id, id);
      return sendSuccess(res, { isSaved: true }, 200);
    } catch (error) {
      next(error);
    }
  }

  async unsaveListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      await listingService.unsaveListing(req.user!.id, id);
      return sendSuccess(res, { isSaved: false }, 200);
    } catch (error) {
      next(error);
    }
  }

  async getSavedListings(req: Request, res: Response, next: NextFunction) {
    try {
      const saved = await listingService.getSavedListings(req.user!.id);
      return sendSuccess(res, { savedListings: saved }, 200);
    } catch (error) {
      next(error);
    }
  }

  async getMyListings(req: Request, res: Response, next: NextFunction) {
    try {
      const listings = await listingService.getMyListings(req.user!.id);
      return sendSuccess(res, { listings }, 200);
    } catch (error) {
      next(error);
    }
  }
}

export const listingController = new ListingController();
