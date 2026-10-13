import { Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { propertyService } from '../services/property.service';
import { sendSuccess } from '../utils/apiResponse';
import { PropertyType, PaymentMethod, ExpenseCategory, MaintenancePriority } from '@prisma/client';

export const createPropertySchema = z.object({
  name: z.string().min(3, 'Property name must be at least 3 characters'),
  propertyType: z.nativeEnum(PropertyType),
  city: z.string().min(2, 'City is required'),
  areaLocation: z.string().min(2, 'Area/Society location is required'),
  address: z.string().min(3, 'Address is required'),
  notes: z.string().optional(),
  managerId: z.string().uuid().optional(),
  initialUnits: z.array(
    z.object({
      unitNumber: z.string().min(1),
      bedrooms: z.number().int().min(0).optional(),
      bathrooms: z.number().int().min(0).optional(),
      monthlyRentPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)])
    })
  ).optional()
});

export const createUnitSchema = z.object({
  unitNumber: z.string().min(1, 'Unit number is required'),
  bedrooms: z.number().int().min(0).optional(),
  bathrooms: z.number().int().min(0).optional(),
  monthlyRentPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)])
});

export const createLeaseSchema = z.object({
  unitId: z.string().uuid(),
  tenantId: z.string().uuid(),
  startDate: z.string().datetime(),
  endDate: z.string().datetime(),
  monthlyRentPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  securityDepositPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  notes: z.string().optional()
});

export const recordManualPaymentSchema = z.object({
  leaseId: z.string().uuid(),
  rentScheduleId: z.string().uuid().optional(),
  amountPaidPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  paymentDate: z.string().datetime(),
  paymentMethod: z.nativeEnum(PaymentMethod),
  referenceNumber: z.string().optional(),
  bankName: z.string().optional(),
  receiptStorageKey: z.string().optional(),
  notes: z.string().optional()
});

export const maintenanceSchema = z.object({
  title: z.string().min(3, 'Title is required'),
  description: z.string().min(5, 'Description is required'),
  priority: z.nativeEnum(MaintenancePriority).optional()
});

export const expenseSchema = z.object({
  category: z.nativeEnum(ExpenseCategory),
  amountPkr: z.union([z.number().positive(), z.string().regex(/^\d+(\.\d{1,2})?$/)]),
  expenseDate: z.string().datetime(),
  title: z.string().min(2, 'Title is required'),
  description: z.string().optional()
});

export class PropertyController {
  async getProperties(req: Request, res: Response, next: NextFunction) {
    try {
      const properties = await propertyService.getProperties(req.user!.id, req.user!.role);
      return sendSuccess(res, { properties }, 200);
    } catch (error) {
      next(error);
    }
  }

  async getPropertyById(req: Request, res: Response, next: NextFunction) {
    try {
      const { id } = req.params;
      const property = await propertyService.getPropertyById(id, req.user!.id, req.user!.role);
      return sendSuccess(res, { property }, 200);
    } catch (error) {
      next(error);
    }
  }

  async createProperty(req: Request, res: Response, next: NextFunction) {
    try {
      const validated = createPropertySchema.parse(req.body);
      const property = await propertyService.createProperty(req.user!.id, validated);
      return sendSuccess(res, { property }, 201);
    } catch (error) {
      next(error);
    }
  }

  async createUnit(req: Request, res: Response, next: NextFunction) {
    try {
      const { propertyId } = req.params;
      const validated = createUnitSchema.parse(req.body);
      const unit = await propertyService.createUnit(propertyId, req.user!.id, req.user!.role, validated);
      return sendSuccess(res, { unit }, 201);
    } catch (error) {
      next(error);
    }
  }

  async createLease(req: Request, res: Response, next: NextFunction) {
    try {
      const validated = createLeaseSchema.parse(req.body);
      const lease = await propertyService.createLease(
        {
          ...validated,
          startDate: new Date(validated.startDate),
          endDate: new Date(validated.endDate)
        },
        req.user!.id
      );
      return sendSuccess(res, { lease }, 201);
    } catch (error) {
      next(error);
    }
  }

  async recordManualPayment(req: Request, res: Response, next: NextFunction) {
    try {
      const validated = recordManualPaymentSchema.parse(req.body);
      const paymentRecord = await propertyService.recordManualPayment(
        {
          ...validated,
          paymentDate: new Date(validated.paymentDate)
        },
        req.user!.id
      );
      return sendSuccess(res, { paymentRecord }, 201);
    } catch (error) {
      next(error);
    }
  }

  async createMaintenanceRequest(req: Request, res: Response, next: NextFunction) {
    try {
      const { unitId } = req.params;
      const validated = maintenanceSchema.parse(req.body);
      const requestTicket = await propertyService.createMaintenanceRequest(unitId, req.user!.id, validated);
      return sendSuccess(res, { maintenanceRequest: requestTicket }, 201);
    } catch (error) {
      next(error);
    }
  }

  async logExpense(req: Request, res: Response, next: NextFunction) {
    try {
      const { propertyId } = req.params;
      const validated = expenseSchema.parse(req.body);
      const expense = await propertyService.logExpense(propertyId, req.user!.id, {
        ...validated,
        expenseDate: new Date(validated.expenseDate)
      });
      return sendSuccess(res, { expense }, 201);
    } catch (error) {
      next(error);
    }
  }

  async getFinancialReport(req: Request, res: Response, next: NextFunction) {
    try {
      const { propertyId } = req.params;
      const report = await propertyService.getPropertyFinancialReport(propertyId);
      return sendSuccess(res, { report }, 200);
    } catch (error) {
      next(error);
    }
  }
}

export const propertyController = new PropertyController();
