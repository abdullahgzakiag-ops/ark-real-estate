import { Prisma, PropertyType, LeaseStatus, RentPaymentStatus, PaymentMethod, ExpenseCategory, MaintenancePriority, MaintenanceStatus } from '@prisma/client';
import { prisma } from '../config/prisma';
import { AppError } from '../utils/apiResponse';

export interface CreatePropertyInput {
  name: string;
  propertyType: PropertyType;
  city: string;
  areaLocation: string;
  address: string;
  notes?: string;
  managerId?: string;
  initialUnits?: Array<{
    unitNumber: string;
    bedrooms?: number;
    bathrooms?: number;
    monthlyRentPkr: number | string;
  }>;
}

export interface CreateUnitInput {
  unitNumber: string;
  bedrooms?: number;
  bathrooms?: number;
  monthlyRentPkr: number | string;
}

export interface CreateLeaseInput {
  unitId: string;
  tenantId: string;
  startDate: Date;
  endDate: Date;
  monthlyRentPkr: number | string;
  securityDepositPkr: number | string;
  notes?: string;
}

export interface RecordManualPaymentInput {
  leaseId: string;
  rentScheduleId?: string;
  amountPaidPkr: number | string;
  paymentDate: Date;
  paymentMethod: PaymentMethod;
  referenceNumber?: string;
  bankName?: string;
  receiptStorageKey?: string;
  notes?: string;
}

export interface MaintenanceInput {
  title: string;
  description: string;
  priority?: MaintenancePriority;
}

export interface ExpenseInput {
  category: ExpenseCategory;
  amountPkr: number | string;
  expenseDate: Date;
  title: string;
  description?: string;
}

export class PropertyService {
  /**
   * Get user's managed/owned properties with occupancy calculations.
   */
  async getProperties(userId: string, userRole: string) {
    const where: Prisma.PropertyWhereInput = {};

    if (userRole !== 'ADMIN') {
      where.OR = [
        { ownerId: userId },
        { managerId: userId }
      ];
    }

    const properties = await prisma.property.findMany({
      where,
      include: {
        units: {
          select: {
            id: true,
            unitNumber: true,
            isOccupied: true,
            monthlyRentPkr: true
          }
        },
        _count: {
          select: { units: true }
        }
      },
      orderBy: { createdAt: 'desc' }
    });

    return properties.map((p: any) => {
      const totalUnits = p.units.length;
      const occupiedUnits = p.units.filter((u: any) => u.isOccupied).length;
      const occupancyRate = totalUnits > 0 ? Math.round((occupiedUnits / totalUnits) * 100) : 0;
      const totalPotentialMonthlyRent = p.units.reduce(
        (sum: number, u: any) => sum + Number(u.monthlyRentPkr),
        0
      );

      return {
        ...p,
        totalUnits,
        occupiedUnits,
        occupancyRate,
        totalPotentialMonthlyRent: totalPotentialMonthlyRent.toFixed(2),
        units: p.units.map((u: any) => ({
          ...u,
          monthlyRentPkr: u.monthlyRentPkr.toString()
        }))
      };
    });
  }

  /**
   * Get property detail with units, active leases, and expenses.
   */
  async getPropertyById(propertyId: string, userId: string, userRole: string) {
    const property = await prisma.property.findUnique({
      where: { id: propertyId },
      include: {
        units: {
          include: {
            leases: {
              where: { status: LeaseStatus.ACTIVE },
              include: {
                tenant: {
                  select: { id: true, fullName: true, phone: true }
                }
              }
            }
          }
        },
        expenses: {
          take: 10,
          orderBy: { expenseDate: 'desc' }
        }
      }
    });

    if (!property) {
      throw new AppError('Property not found', 404, 'NOT_FOUND');
    }

    if (userRole !== 'ADMIN' && property.ownerId !== userId && property.managerId !== userId) {
      throw new AppError('Access denied for this property', 403, 'FORBIDDEN');
    }

    const totalUnits = property.units.length;
    const occupiedUnits = property.units.filter((u: any) => u.isOccupied).length;
    const occupancyRate = totalUnits > 0 ? Math.round((occupiedUnits / totalUnits) * 100) : 0;

    return {
      ...property,
      totalUnits,
      occupiedUnits,
      occupancyRate,
      units: property.units.map((u: any) => ({
        ...u,
        monthlyRentPkr: u.monthlyRentPkr.toString(),
        leases: u.leases.map((l: any) => ({
          ...l,
          monthlyRentPkr: l.monthlyRentPkr.toString(),
          securityDepositPkr: l.securityDepositPkr.toString()
        }))
      })),
      expenses: property.expenses.map((e: any) => ({
        ...e,
        amountPkr: e.amountPkr.toString()
      }))
    };
  }

  /**
   * Create a new property portfolio entry.
   */
  async createProperty(userId: string, data: CreatePropertyInput) {
    return await prisma.property.create({
      data: {
        ownerId: userId,
        managerId: data.managerId,
        name: data.name,
        propertyType: data.propertyType,
        city: data.city,
        areaLocation: data.areaLocation,
        address: data.address,
        notes: data.notes,
        units: data.initialUnits?.length
          ? {
              create: data.initialUnits.map((u: any) => ({
                unitNumber: u.unitNumber,
                bedrooms: u.bedrooms,
                bathrooms: u.bathrooms,
                monthlyRentPkr: new Prisma.Decimal(u.monthlyRentPkr),
                isOccupied: false
              }))
            }
          : undefined
      },
      include: {
        units: true
      }
    });
  }

  /**
   * Create a unit within a property.
   */
  async createUnit(propertyId: string, userId: string, userRole: string, data: CreateUnitInput) {
    const property = await prisma.property.findUnique({ where: { id: propertyId } });
    if (!property) {
      throw new AppError('Property not found', 404, 'NOT_FOUND');
    }

    if (userRole !== 'ADMIN' && property.ownerId !== userId && property.managerId !== userId) {
      throw new AppError('Access denied', 403, 'FORBIDDEN');
    }

    const unit = await prisma.unit.create({
      data: {
        propertyId,
        unitNumber: data.unitNumber,
        bedrooms: data.bedrooms,
        bathrooms: data.bathrooms,
        monthlyRentPkr: new Prisma.Decimal(data.monthlyRentPkr),
        isOccupied: false
      }
    });

    return {
      ...unit,
      monthlyRentPkr: unit.monthlyRentPkr.toString()
    };
  }

  /**
   * Create lease agreement and automatically generate monthly Rent Schedules.
   */
  async createLease(data: CreateLeaseInput, createdByUserId: string) {
    const unit = await prisma.unit.findUnique({
      where: { id: data.unitId },
      include: { property: true }
    });

    if (!unit) {
      throw new AppError('Unit not found', 404, 'NOT_FOUND');
    }

    const start = new Date(data.startDate);
    const end = new Date(data.endDate);

    if (end <= start) {
      throw new AppError('Lease end date must be after start date', 400, 'BAD_REQUEST');
    }

    // Generate monthly rent schedules
    const schedulesToCreate: Array<{
      dueDate: Date;
      amountDuePkr: Prisma.Decimal;
      status: RentPaymentStatus;
    }> = [];

    const currentMonth = new Date(start);
    while (currentMonth < end) {
      schedulesToCreate.push({
        dueDate: new Date(currentMonth),
        amountDuePkr: new Prisma.Decimal(data.monthlyRentPkr),
        status: RentPaymentStatus.PENDING
      });
      currentMonth.setMonth(currentMonth.getMonth() + 1);
    }

    const [lease] = await prisma.$transaction([
      prisma.lease.create({
        data: {
          unitId: data.unitId,
          tenantId: data.tenantId,
          startDate: start,
          endDate: end,
          monthlyRentPkr: new Prisma.Decimal(data.monthlyRentPkr),
          securityDepositPkr: new Prisma.Decimal(data.securityDepositPkr),
          status: LeaseStatus.ACTIVE,
          notes: data.notes,
          rentSchedules: {
            create: schedulesToCreate
          }
        },
        include: {
          rentSchedules: {
            orderBy: { dueDate: 'asc' }
          },
          tenant: {
            select: { id: true, fullName: true, phone: true }
          }
        }
      }),
      prisma.unit.update({
        where: { id: data.unitId },
        data: { isOccupied: true }
      })
    ]);

    return {
      ...lease,
      monthlyRentPkr: lease.monthlyRentPkr.toString(),
      securityDepositPkr: lease.securityDepositPkr.toString(),
      rentSchedules: lease.rentSchedules.map((rs: any) => ({
        ...rs,
        amountDuePkr: rs.amountDuePkr.toString(),
        amountPaidPkr: rs.amountPaidPkr.toString()
      }))
    };
  }

  /**
   * Rule 18: Record manual rent payment record.
   * ARK does not transfer or process money; this strictly writes an audited manual receipt.
   */
  async recordManualPayment(data: RecordManualPaymentInput, recordedByUserId: string) {
    const lease = await prisma.lease.findUnique({
      where: { id: data.leaseId },
      include: { unit: { include: { property: true } } }
    });

    if (!lease) {
      throw new AppError('Lease not found', 404, 'NOT_FOUND');
    }

    const amountPaid = new Prisma.Decimal(data.amountPaidPkr);

    const result = await prisma.$transaction(async (tx: Prisma.TransactionClient) => {
      // Create manual payment record
      const paymentRecord = await tx.manualPaymentRecord.create({
        data: {
          leaseId: data.leaseId,
          rentScheduleId: data.rentScheduleId,
          recordedByUserId,
          amountPaidPkr: amountPaid,
          paymentDate: new Date(data.paymentDate),
          paymentMethod: data.paymentMethod,
          referenceNumber: data.referenceNumber,
          bankName: data.bankName,
          receiptStorageKey: data.receiptStorageKey,
          notes: data.notes,
          isVerified: false
        }
      });

      // If tied to a specific rent schedule, update paid amount & status
      if (data.rentScheduleId) {
        const schedule = await tx.rentSchedule.findUnique({
          where: { id: data.rentScheduleId }
        });

        if (schedule) {
          const newPaid = schedule.amountPaidPkr.add(amountPaid);
          const isFullyPaid = newPaid.greaterThanOrEqualTo(schedule.amountDuePkr);

          await tx.rentSchedule.update({
            where: { id: data.rentScheduleId },
            data: {
              amountPaidPkr: newPaid,
              status: isFullyPaid ? RentPaymentStatus.PAID : RentPaymentStatus.PARTIAL
            }
          });
        }
      }

      // Record audit log
      await tx.auditLog.create({
        data: {
          userId: recordedByUserId,
          action: 'MANUAL_PAYMENT_LOGGED',
          entityType: 'ManualPaymentRecord',
          entityId: paymentRecord.id,
          metadata: {
            leaseId: data.leaseId,
            amount: amountPaid.toString(),
            paymentMethod: data.paymentMethod,
            referenceNumber: data.referenceNumber,
            complianceNotice: 'Rule 18: Manual payment entry recorded without fund custody.'
          }
        }
      });

      return paymentRecord;
    });

    return {
      ...result,
      amountPaidPkr: result.amountPaidPkr.toString(),
      complianceDisclosure: 'Payment logged manually. Subject to verification by ARK custodian before clearance. ARK does not transfer or process funds.'
    };
  }

  /**
   * Log maintenance request ticket.
   */
  async createMaintenanceRequest(unitId: string, reportedById: string, data: MaintenanceInput) {
    const unit = await prisma.unit.findUnique({ where: { id: unitId } });
    if (!unit) {
      throw new AppError('Unit not found', 404, 'NOT_FOUND');
    }

    return await prisma.maintenanceRequest.create({
      data: {
        unitId,
        reportedById,
        title: data.title,
        description: data.description,
        priority: data.priority ?? MaintenancePriority.MEDIUM,
        status: MaintenanceStatus.OPEN
      }
    });
  }

  /**
   * Log operating property expense (utility, repair, tax).
   */
  async logExpense(propertyId: string, loggedById: string, data: ExpenseInput) {
    const property = await prisma.property.findUnique({ where: { id: propertyId } });
    if (!property) {
      throw new AppError('Property not found', 404, 'NOT_FOUND');
    }

    const expense = await prisma.expense.create({
      data: {
        propertyId,
        loggedById,
        category: data.category,
        amountPkr: new Prisma.Decimal(data.amountPkr),
        expenseDate: new Date(data.expenseDate),
        title: data.title,
        description: data.description
      }
    });

    return {
      ...expense,
      amountPkr: expense.amountPkr.toString()
    };
  }

  /**
   * Financial yield report: rent collected vs logged expenses.
   */
  async getPropertyFinancialReport(propertyId: string) {
    const property = await prisma.property.findUnique({
      where: { id: propertyId },
      include: {
        units: {
          include: {
            leases: {
              include: {
                rentSchedules: true
              }
            }
          }
        },
        expenses: true
      }
    });

    if (!property) {
      throw new AppError('Property not found', 404, 'NOT_FOUND');
    }

    let totalRentCollected = new Prisma.Decimal(0);
    let totalRentDue = new Prisma.Decimal(0);

    property.units.forEach((u: any) => {
      u.leases.forEach((l: any) => {
        l.rentSchedules.forEach((rs: any) => {
          totalRentDue = totalRentDue.add(rs.amountDuePkr);
          totalRentCollected = totalRentCollected.add(rs.amountPaidPkr);
        });
      });
    });

    let totalExpenses = new Prisma.Decimal(0);
    property.expenses.forEach((e: any) => {
      totalExpenses = totalExpenses.add(e.amountPkr);
    });

    const netOperatingIncomePkr = totalRentCollected.sub(totalExpenses);

    return {
      propertyId,
      propertyName: property.name,
      currency: 'PKR',
      totalRentDue: totalRentDue.toString(),
      totalRentCollected: totalRentCollected.toString(),
      totalExpenses: totalExpenses.toString(),
      netOperatingIncomePkr: netOperatingIncomePkr.toString(),
      expensesBreakdown: property.expenses.map((e: any) => ({
        id: e.id,
        category: e.category,
        title: e.title,
        amountPkr: e.amountPkr.toString(),
        expenseDate: e.expenseDate
      }))
    };
  }
}

export const propertyService = new PropertyService();
