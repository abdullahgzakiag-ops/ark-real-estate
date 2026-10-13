import request from 'supertest';
import express from 'express';
import { z } from 'zod';
import { validateRequest } from '../src/middlewares/validateRequest';
import { errorHandler } from '../src/middlewares/errorHandler';
import { AppError } from '../src/utils/apiResponse';

describe('Middleware Unit & Integration Tests', () => {
  const app = express();
  app.use(express.json());

  // Test route for validation
  const testSchema = {
    body: z.object({
      title: z.string().min(3, 'Title must be at least 3 characters'),
      pricePkr: z.number().positive('Price must be greater than zero'),
    }),
  };

  app.post('/test/validate', validateRequest(testSchema), (_req, res) => {
    res.json({ success: true, message: 'Valid' });
  });

  // Test route for AppError
  app.get('/test/app-error', () => {
    throw new AppError('Unauthorized access', 401, 'UNAUTHORIZED');
  });

  // Test route for unhandled error
  app.get('/test/unhandled-error', () => {
    throw new Error('Database connection broke unexpectedly');
  });

  app.use(errorHandler);

  describe('validateRequest middleware', () => {
    it('should return 422 with detailed field issues when validation fails', async () => {
      const res = await request(app).post('/test/validate').send({
        title: 'Hi',
        pricePkr: -500,
      });

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
      expect(res.body.error.details).toBeDefined();
      expect(res.body.error.details.length).toBe(2);
    });

    it('should pass validation when payload is correct', async () => {
      const res = await request(app).post('/test/validate').send({
        title: 'Modern Villa',
        pricePkr: 25000000,
      });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
    });
  });

  describe('errorHandler middleware', () => {
    it('should catch AppError and return corresponding status code and code', async () => {
      const res = await request(app).get('/test/app-error');

      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('UNAUTHORIZED');
      expect(res.body.error.message).toBe('Unauthorized access');
    });

    it('should catch unhandled Error and return 500 INTERNAL_SERVER_ERROR', async () => {
      const res = await request(app).get('/test/unhandled-error');

      expect(res.status).toBe(500);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('INTERNAL_SERVER_ERROR');
    });
  });
});
