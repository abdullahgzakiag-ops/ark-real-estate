import { Router } from 'express';
import { moderationController } from '../controllers/moderation.controller';
import { authenticate, requireRole } from '../middlewares/auth.middleware';
import { UserRole } from '@prisma/client';

const router = Router();

// All moderation routes are strictly restricted to ADMIN role
router.use(authenticate);
router.use(requireRole([UserRole.ADMIN]));

router.get('/listings', (req, res, next) => moderationController.getPendingQueue(req, res, next));
router.patch('/listings/:id/approve', (req, res, next) => moderationController.approveListing(req, res, next));
router.patch('/listings/:id/reject', (req, res, next) => moderationController.rejectListing(req, res, next));

export default router;
