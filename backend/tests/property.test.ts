import request from 'supertest';
import { createApp } from '../src/app';
import { prisma } from '../src/config/prisma';
import { AuthService } from '../src/services/auth.service';
import { UserRole, PropertyType, LeaseStatus, RentPaymentStatus, PaymentMethod, ExpenseCategory, Prisma } from '@prisma/client';

describe('Property Management & Tenancy Ledger API (/v1/properties)', () => {
  const app = createApp();

  let ownerToken: string;
  let tenantToken: string;
  let managerToken: string;

  let ownerUser: any;
  let tenantUser: any;
  let managerUser: any;

  beforeAll(async () => {
    ownerUser = {
      id: 'owner-uuid-1',
      role: UserRole.OWNER_AGENT,
      phone: '+923004444444',
      fullName: 'Kamran Landlord'
    };
    ownerToken = AuthService.generateTokens(ownerUser).accessToken;

    tenantUser = {
      id: 'tenant-uuid-2',
      role: UserRole.BUYER_RENTER,
      phone: '+923005555555',
      fullName: 'Bilal Tenant'
    };
    tenantToken = AuthService.generateTokens(tenantUser).accessToken;

    managerUser = {
      id: 'manager-uuid-3',
      role: UserRole.PROPERTY_MANAGER,
      phone: '+923006666666',
      fullName: 'Saeed Property Manager'
    };
    managerToken = AuthService.generateTokens(managerUser).accessToken;
  });

  describe('POST /v1/properties (Create Property Portfolio Entry)', () => {
    it('should create property with units and decimal money fields', async () => {
      const mockCreatedProperty = {
        id: 'prop-1',
        ownerId: ownerUser.id,
        name: 'Gulberg Heights Residency',
        propertyType: PropertyType.APARTMENT,
        city: 'Lahore',
        areaLocation: 'Main Boulevard Gulberg',
        address: 'Plot 42, Block B-3, Gulberg III',
        notes: 'Commercial/Residential mixed building',
        createdAt: new Date(),
        updatedAt: new Date(),
        units: [
          {
            id: 'unit-1',
            propertyId: 'prop-1',
            unitNumber: 'Apartment 401',
            bedrooms: 2,
            bathrooms: 2,
            monthlyRentPkr: '85000.00',
            isOccupied: false
          }
        ]
      };

      jest.spyOn(prisma.property, 'create').mockResolvedValue(mockCreatedProperty as any);

      const res = await request(app)
        .post('/v1/properties')
        .set('Authorization', `Bearer ${ownerToken}`)
        .send({
          name: 'Gulberg Heights Residency',
          propertyType: 'APARTMENT',
          city: 'Lahore',
          areaLocation: 'Main Boulevard Gulberg',
          address: 'Plot 42, Block B-3, Gulberg III',
          initialUnits: [
            {
              unitNumber: 'Apartment 401',
              bedrooms: 2,
              bathrooms: 2,
              monthlyRentPkr: 85000
            }
          ]
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.property.name).toBe('Gulberg Heights Residency');
    });
  });

  describe('POST /v1/properties/leases (Create Lease & Auto-generate Rent Schedule)', () => {
    it('should create lease agreement and generate monthly rent schedules', async () => {
      jest.spyOn(prisma.unit, 'findUnique').mockResolvedValue({
        id: 'unit-1',
        propertyId: 'prop-1',
        unitNumber: 'Apartment 401',
        isOccupied: false
      } as any);

      const mockLease = {
        id: 'lease-1',
        unitId: 'unit-1',
        tenantId: tenantUser.id,
        startDate: new Date('2026-11-01T00:00:00Z'),
        endDate: new Date('2027-01-01T00:00:00Z'),
        monthlyRentPkr: '85000.00',
        securityDepositPkr: '170000.00',
        status: LeaseStatus.ACTIVE,
        rentSchedules: [
          {
            id: 'rs-1',
            leaseId: 'lease-1',
            dueDate: new Date('2026-11-01T00:00:00Z'),
            amountDuePkr: '85000.00',
            amountPaidPkr: '0.00',
            status: RentPaymentStatus.PENDING
          },
          {
            id: 'rs-2',
            leaseId: 'lease-1',
            dueDate: new Date('2026-12-01T00:00:00Z'),
            amountDuePkr: '85000.00',
            amountPaidPkr: '0.00',
            status: RentPaymentStatus.PENDING
          }
        ],
        tenant: {
          id: tenantUser.id,
          fullName: tenantUser.fullName,
          phone: tenantUser.phone
        }
      };

      jest.spyOn(prisma, '$transaction').mockResolvedValue([mockLease, {}] as any);

      const res = await request(app)
        .post('/v1/properties/leases')
        .set('Authorization', `Bearer ${managerToken}`)
        .send({
          unitId: '11111111-1111-1111-1111-111111111111',
          tenantId: '22222222-2222-2222-2222-222222222222',
          startDate: '2026-11-01T00:00:00.000Z',
          endDate: '2027-01-01T00:00:00.000Z',
          monthlyRentPkr: 85000,
          securityDepositPkr: 170000
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.lease.rentSchedules).toHaveLength(2);
      expect(res.body.data.lease.rentSchedules[0].amountDuePkr).toBe('85000.00');
    });
  });

  describe('POST /v1/properties/payments/manual (Rule 18: Manual Rent Recording)', () => {
    it('should record manual payment with explicit Rule 18 compliance disclosure and audit trail', async () => {
      jest.spyOn(prisma.lease, 'findUnique').mockResolvedValue({
        id: 'lease-1',
        monthlyRentPkr: '85000.00'
      } as any);

      const mockPaymentRecord = {
        id: 'manual-pay-1',
        leaseId: 'lease-1',
        amountPaidPkr: '85000.00',
        paymentDate: new Date('2026-11-02T10:00:00Z'),
        paymentMethod: PaymentMethod.BANK_TRANSFER,
        referenceNumber: 'HBL-FT-984210',
        bankName: 'Habib Bank Limited',
        isVerified: false
      };

      jest.spyOn(prisma, '$transaction').mockImplementation(async (callback: any) => {
        return callback({
          manualPaymentRecord: {
            create: jest.fn().mockResolvedValue(mockPaymentRecord)
          },
          rentSchedule: {
            findUnique: jest.fn().mockResolvedValue({
              id: 'rs-1',
              amountDuePkr: { add: () => ({ greaterThanOrEqualTo: () => true }) },
              amountPaidPkr: { add: () => ({ greaterThanOrEqualTo: () => true, toString: () => '85000.00' }) }
            }),
            update: jest.fn().mockResolvedValue({})
          },
          auditLog: {
            create: jest.fn().mockResolvedValue({})
          }
        });
      });

      const res = await request(app)
        .post('/v1/properties/payments/manual')
        .set('Authorization', `Bearer ${managerToken}`)
        .send({
          leaseId: '11111111-1111-1111-1111-111111111111',
          rentScheduleId: '33333333-3333-3333-3333-333333333333',
          amountPaidPkr: 85000,
          paymentDate: '2026-11-02T10:00:00.000Z',
          paymentMethod: 'BANK_TRANSFER',
          referenceNumber: 'HBL-FT-984210',
          bankName: 'Habib Bank Limited'
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.paymentRecord.complianceDisclosure).toContain('Payment logged manually');
      expect(res.body.data.paymentRecord.complianceDisclosure).toContain('ARK does not transfer or process funds');
    });
  });

  describe('POST /v1/properties/:propertyId/expenses & Financial Reports', () => {
    it('should log operating expenses and calculate net yield report', async () => {
      jest.spyOn(prisma.property, 'findUnique').mockResolvedValue({
        id: 'prop-1',
        name: 'Gulberg Heights Residency',
        units: [
          {
            leases: [
              {
                rentSchedules: [
                  {
                    amountDuePkr: new Prisma.Decimal('85000.00'),
                    amountPaidPkr: new Prisma.Decimal('85000.00')
                  }
                ]
              }
            ]
          }
        ],
        expenses: [
          {
            id: 'exp-1',
            category: ExpenseCategory.MAINTENANCE,
            title: 'Lift Servicing',
            amountPkr: new Prisma.Decimal('15000.00'),
            expenseDate: new Date('2026-11-10')
          }
        ]
      } as any);

      const res = await request(app)
        .get('/v1/properties/prop-1/financial-report')
        .set('Authorization', `Bearer ${ownerToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.report.currency).toBe('PKR');
    });
  });
});
