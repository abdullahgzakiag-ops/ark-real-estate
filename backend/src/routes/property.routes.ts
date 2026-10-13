import { Router } from 'express';
import { propertyController } from '../controllers/property.controller';
import { authenticate, requireRole } from '../middlewares/auth.middleware';
import { UserRole } from '@prisma/client';

const router = Router();

// Enforce authentication across all property management endpoints
router.use(authenticate);

// Properties
router.get('/', (req, res, next) => propertyController.getProperties(req, res, next));
router.post(
  '/',
  requireRole([UserRole.OWNER_AGENT, UserRole.PROPERTY_MANAGER, UserRole.ADMIN]),
  (req, res, next) => propertyController.createProperty(req, res, next)
);
router.get('/:id', (req, res, next) => propertyController.getPropertyById(req, res, next));

// Units
router.post(
  '/:propertyId/units',
  requireRole([UserRole.OWNER_AGENT, UserRole.PROPERTY_MANAGER, UserRole.ADMIN]),
  (req, res, next) => propertyController.createUnit(req, res, next)
);

// Leases & Tenancy
router.post(
  '/leases',
  requireRole([UserRole.OWNER_AGENT, UserRole.PROPERTY_MANAGER, UserRole.ADMIN]),
  (req, res, next) => propertyController.createLease(req, res, next)
);

// Manual Rent Payments (Rule 18: No fund custody, manual receipt audit log)
router.post('/payments/manual', (req, res, next) => propertyController.recordManualPayment(req, res, next));

// Maintenance
router.post('/units/:unitId/maintenance', (req, res, next) => propertyController.createMaintenanceRequest(req, res, next));

// Expenses & Yield Reports
router.post(
  '/:propertyId/expenses',
  requireRole([UserRole.OWNER_AGENT, UserRole.PROPERTY_MANAGER, UserRole.ADMIN]),
  (req, res, next) => propertyController.logExpense(req, res, next)
);
router.get('/:propertyId/financial-report', (req, res, next) => propertyController.getFinancialReport(req, res, next));

export default router;
