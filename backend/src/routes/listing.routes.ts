import { Router } from 'express';
import { listingController } from '../controllers/listing.controller';
import { authenticate, requireRole } from '../middlewares/auth.middleware';
import { UserRole } from '@prisma/client';

const router = Router();

// Public Marketplace Search & Listing Detail
router.get('/', (req, res, next) => listingController.getListings(req, res, next));
router.get('/user/saved', authenticate, (req, res, next) => listingController.getSavedListings(req, res, next));
router.get('/user/my-listings', authenticate, (req, res, next) => listingController.getMyListings(req, res, next));
router.get('/:id', (req, res, next) => listingController.getListingById(req, res, next));

// Protected: Listing Submission (Owner, Agent or Admin)
router.post(
  '/',
  authenticate,
  requireRole([UserRole.OWNER_AGENT, UserRole.ADMIN, UserRole.PROPERTY_MANAGER]),
  (req, res, next) => listingController.createListing(req, res, next)
);

// Protected: Modify or Delete Listing
router.put('/:id', authenticate, (req, res, next) => listingController.updateListing(req, res, next));
router.delete('/:id', authenticate, (req, res, next) => listingController.deleteListing(req, res, next));

// Protected: Inquiries & Viewing Scheduling
router.post('/:id/inquiries', authenticate, (req, res, next) => listingController.createInquiry(req, res, next));
router.post('/:id/viewings', authenticate, (req, res, next) => listingController.createViewingRequest(req, res, next));

// Protected: Bookmarking / Saved Listings
router.post('/:id/save', authenticate, (req, res, next) => listingController.saveListing(req, res, next));
router.delete('/:id/save', authenticate, (req, res, next) => listingController.unsaveListing(req, res, next));

export default router;
