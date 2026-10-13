import { Router, Request, Response } from 'express';
import { sendSuccess } from '../utils/apiResponse';

const router = Router();

router.get('/', (_req: Request, res: Response) => {
  const healthData = {
    status: 'ok',
    service: 'ARK Real Estate API',
    version: '1.0.0',
    environment: process.env.NODE_ENV || 'development',
    timestamp: new Date().toISOString(),
    uptimeSeconds: Math.floor(process.uptime()),
    database: {
      provider: 'postgresql',
      status: 'configured',
    },
  };

  sendSuccess(res, healthData, 200);
});

export const healthRoutes = router;
