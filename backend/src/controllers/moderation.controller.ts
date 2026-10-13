import { Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { listingService } from '../services/listing.service';
import { sendSuccess } from '../utils/apiResponse';

export const rejectListingSchema = z.object({
  reason: z.string().min(5, 'Rejection reason must be at least 5 characters')
});

export const approveListingSchema = z.object({
  isVerifiedBadge: z.boolean().optional().default(true)
});

export class ModerationController {
  async getPendingQueue(req: Request, res: Response, next: NextFunction) {
    try {
      const page = Math.max(1, Number(req.query.page) || 1);
      const limit = Math.min(50, Math.max(1, Number(req.query.limit) || 20));
      const result = await listingService.getPendingModerationQueue(page, limit);
      return sendSuccess(res, result.listings, 200, result.pagination);
    } catch (error) {
      next(error);
    }
  }

  async approveListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const { isVerifiedBadge } = approveListingSchema.parse(req.body || {});
      const listing = await listingService.moderateListing(
        id,
        req.user!.id,
        'APPROVE',
        undefined,
        isVerifiedBadge
      );
      return sendSuccess(res, { listing }, 200);
    } catch (error) {
      next(error);
    }
  }

  async rejectListing(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const { reason } = rejectListingSchema.parse(req.body);
      const listing = await listingService.moderateListing(
        id,
        req.user!.id,
        'REJECT',
        reason,
        false
      );
      return sendSuccess(res, { listing }, 200);
    } catch (error) {
      next(error);
    }
  }
}

export const moderationController = new ModerationController();
