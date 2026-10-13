import { Router } from 'express';
import rateLimit from 'express-rate-limit';
import {
  AuthController,
  registerSchema,
  loginSchema,
  refreshTokenSchema,
} from '../controllers/auth.controller';
import { validateRequest } from '../middlewares/validateRequest';
import { authenticate } from '../middlewares/auth.middleware';

const router = Router();

// Rate limiter for login endpoint to mitigate credential-stuffing and brute-force
const authRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: process.env.NODE_ENV === 'test' ? 1000 : 10, // Generous limit in tests
  standardHeaders: true,
  legacyHeaders: false,
  message: {
    success: false,
    error: {
      code: 'TOO_MANY_REQUESTS',
      message: 'Too many authentication attempts. Please try again after 15 minutes.',
    },
  },
});

router.post(
  '/register',
  validateRequest({ body: registerSchema }),
  AuthController.register
);

router.post(
  '/login',
  authRateLimiter,
  validateRequest({ body: loginSchema }),
  AuthController.login
);

router.post(
  '/refresh',
  validateRequest({ body: refreshTokenSchema }),
  AuthController.refreshToken
);

router.get(
  '/me',
  authenticate,
  AuthController.getMe
);

export const authRoutes = router;
