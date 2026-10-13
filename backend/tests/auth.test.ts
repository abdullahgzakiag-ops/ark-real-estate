import request from 'supertest';
import express from 'express';
import { UserRole } from '@prisma/client';
import { AuthService } from '../src/services/auth.service';
import { authenticate, requireRole } from '../src/middlewares/auth.middleware';
import { createApp } from '../src/app';

describe('Auth & RBAC Foundation Tests', () => {
  const app = createApp();

  describe('AuthService Cryptography & Token Helpers', () => {
    it('should hash password and verify correctly', async () => {
      const plaintext = 'SuperSecret123!';
      const hash = await AuthService.hashPassword(plaintext);

      expect(hash).not.toBe(plaintext);
      expect(hash.startsWith('$2')).toBe(true);

      const isMatch = await AuthService.verifyPassword(plaintext, hash);
      expect(isMatch).toBe(true);

      const isBadMatch = await AuthService.verifyPassword('WrongPass', hash);
      expect(isBadMatch).toBe(false);
    });

    it('should normalize Pakistani phone numbers correctly', () => {
      expect(AuthService.normalizePhone('03001234567')).toBe('+923001234567');
      expect(AuthService.normalizePhone('+923001234567')).toBe('+923001234567');
      expect(AuthService.normalizePhone('923001234567')).toBe('+923001234567');
      expect(AuthService.normalizePhone('0300 123 4567')).toBe('+923001234567');
    });

    it('should generate valid access and refresh tokens and decode them', () => {
      const userPayload = {
        id: 'test-user-123',
        role: UserRole.OWNER_AGENT,
        phone: '+923001234567',
        email: 'agent@example.pk',
      };

      const tokens = AuthService.generateTokens(userPayload);
      expect(tokens.accessToken).toBeDefined();
      expect(tokens.refreshToken).toBeDefined();

      const decodedAccess = AuthService.verifyAccessToken(tokens.accessToken);
      expect(decodedAccess.id).toBe(userPayload.id);
      expect(decodedAccess.role).toBe(UserRole.OWNER_AGENT);

      const decodedRefresh = AuthService.verifyRefreshToken(tokens.refreshToken);
      expect(decodedRefresh.id).toBe(userPayload.id);
    });

    it('should sanitize user object to never leak passwordHash', () => {
      const mockUser = {
        id: 'uuid-1',
        email: 'user@example.com',
        phone: '+923001234567',
        passwordHash: '$2a$10$insecureMockHash1234567890',
        fullName: 'Zaid Khan',
        role: UserRole.BUYER_RENTER,
        agencyName: null,
        avatarUrl: null,
        isPhoneVerified: false,
        preferredLocale: 'en',
        createdAt: new Date(),
        updatedAt: new Date(),
      };

      const sanitized = AuthService.sanitizeUser(mockUser);
      expect(sanitized).not.toHaveProperty('passwordHash');
      expect(sanitized.id).toBe('uuid-1');
      expect(sanitized.fullName).toBe('Zaid Khan');
    });
  });

  describe('RBAC & Auth Middleware Enforcement', () => {
    const testApp = express();
    testApp.use(express.json());

    // Protected route requiring BUYER_RENTER or OWNER_AGENT
    testApp.get('/test/protected', authenticate, (req, res) => {
      res.json({ success: true, user: req.user });
    });

    // Admin-only route
    testApp.get('/test/admin-only', authenticate, requireRole([UserRole.ADMIN]), (_req, res) => {
      res.json({ success: true, message: 'Welcome Admin' });
    });

    it('should block unauthenticated requests with 401', async () => {
      const res = await request(testApp).get('/test/protected');
      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('UNAUTHORIZED');
    });

    it('should block invalid tokens with 401', async () => {
      const res = await request(testApp)
        .get('/test/protected')
        .set('Authorization', 'Bearer invalid.token.payload');
      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('UNAUTHORIZED');
    });

    it('should allow authenticated user through', async () => {
      const tokens = AuthService.generateTokens({
        id: 'user-88',
        role: UserRole.BUYER_RENTER,
        phone: '+923001234567',
      });

      const res = await request(testApp)
        .get('/test/protected')
        .set('Authorization', `Bearer ${tokens.accessToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.user.id).toBe('user-88');
    });

    it('should block non-admin users from admin routes with 403 FORBIDDEN', async () => {
      const buyerTokens = AuthService.generateTokens({
        id: 'buyer-1',
        role: UserRole.BUYER_RENTER,
        phone: '+923001234567',
      });

      const res = await request(testApp)
        .get('/test/admin-only')
        .set('Authorization', `Bearer ${buyerTokens.accessToken}`);

      expect(res.status).toBe(403);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('FORBIDDEN');
    });

    it('should allow admin users through to admin routes', async () => {
      const adminTokens = AuthService.generateTokens({
        id: 'admin-1',
        role: UserRole.ADMIN,
        phone: '+923009999999',
      });

      const res = await request(testApp)
        .get('/test/admin-only')
        .set('Authorization', `Bearer ${adminTokens.accessToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.message).toBe('Welcome Admin');
    });
  });

  describe('Auth Endpoints Request Validation', () => {
    it('POST /v1/auth/register should fail 422 if phone is not a valid Pakistani number', async () => {
      const res = await request(app).post('/v1/auth/register').send({
        phone: '12345',
        password: 'Password123!',
        fullName: 'Test User',
      });

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });

    it('POST /v1/auth/register should fail 422 if password is shorter than 8 characters', async () => {
      const res = await request(app).post('/v1/auth/register').send({
        phone: '03001234567',
        password: 'short',
        fullName: 'Test User',
      });

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });

    it('POST /v1/auth/login should fail 422 if fields are missing', async () => {
      const res = await request(app).post('/v1/auth/login').send({
        phoneOrEmail: '',
      });

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });

    it('POST /v1/auth/refresh should fail 422 if refreshToken is missing', async () => {
      const res = await request(app).post('/v1/auth/refresh').send({});

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });

    it('POST /v1/auth/refresh should rotate access token with valid refreshToken', async () => {
      const userPayload = {
        id: 'user-refresh-1',
        role: UserRole.OWNER_AGENT,
        phone: '+923001234567',
      };
      const tokens = AuthService.generateTokens(userPayload);

      const res = await request(app).post('/v1/auth/refresh').send({
        refreshToken: tokens.refreshToken,
      });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.tokens).toHaveProperty('accessToken');
      expect(res.body.data.tokens).toHaveProperty('refreshToken');
    });
  });
});
