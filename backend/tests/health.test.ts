import request from 'supertest';
import { createApp } from '../src/app';

describe('Health and API Foundation Integration Tests', () => {
  const app = createApp();

  describe('GET /v1/health', () => {
    it('should return 200 and report healthy status', async () => {
      const res = await request(app).get('/v1/health');

      expect(res.status).toBe(200);
      expect(res.body).toHaveProperty('success', true);
      expect(res.body.data).toHaveProperty('status', 'ok');
      expect(res.body.data).toHaveProperty('service', 'ARK Real Estate API');
      expect(res.body.data).toHaveProperty('version', '1.0.0');
      expect(res.body.data).toHaveProperty('timestamp');
      expect(res.body.data).toHaveProperty('uptimeSeconds');
      expect(res.body.data.database).toHaveProperty('provider', 'postgresql');
    });
  });

  describe('GET /v1/nonexistent-route (404 Handling)', () => {
    it('should return 404 with standardized error envelope', async () => {
      const res = await request(app).get('/v1/nonexistent-route');

      expect(res.status).toBe(404);
      expect(res.body).toHaveProperty('success', false);
      expect(res.body.error).toHaveProperty('code', 'NOT_FOUND');
      expect(res.body.error).toHaveProperty('message', 'Resource not found');
    });
  });
});
