import { Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { UserRole } from '@prisma/client';
import { AuthService } from '../services/auth.service';
import { sendSuccess } from '../utils/apiResponse';

export const registerSchema = z.object({
  phone: z
    .string()
    .trim()
    .regex(/^(\+92|0|92)?3[0-9]{9}$/, 'Please provide a valid Pakistani mobile number (e.g. 03001234567 or +923001234567)'),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters long'),
  fullName: z
    .string()
    .trim()
    .min(2, 'Full name must be at least 2 characters long'),
  email: z
    .string()
    .trim()
    .email('Please provide a valid email address')
    .optional(),
  role: z
    .nativeEnum(UserRole)
    .optional()
    .default(UserRole.BUYER_RENTER),
  agencyName: z
    .string()
    .trim()
    .optional(),
  preferredLocale: z
    .enum(['en', 'ur'])
    .optional()
    .default('en'),
});

export const loginSchema = z.object({
  phoneOrEmail: z
    .string()
    .trim()
    .min(3, 'Phone number or email is required'),
  password: z
    .string()
    .min(1, 'Password is required'),
});

export const refreshTokenSchema = z.object({
  refreshToken: z
    .string()
    .min(1, 'Refresh token is required'),
});

export class AuthController {
  public static async register(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const result = await AuthService.register(req.body);
      sendSuccess(res, result, 201);
    } catch (error) {
      next(error);
    }
  }

  public static async login(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const result = await AuthService.login(req.body);
      sendSuccess(res, result, 200);
    } catch (error) {
      next(error);
    }
  }

  public static async refreshToken(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const { refreshToken } = req.body;
      const decoded = AuthService.verifyRefreshToken(refreshToken);

      // Issue new token pair
      const tokens = AuthService.generateTokens({
        id: decoded.id,
        role: decoded.role,
        phone: decoded.phone,
        email: decoded.email,
      });

      sendSuccess(res, { tokens }, 200);
    } catch (error) {
      next(error);
    }
  }

  public static async getMe(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      if (!req.user) {
        throw new Error('User context missing');
      }
      const user = await AuthService.getUserById(req.user.id);
      sendSuccess(res, { user }, 200);
    } catch (error) {
      next(error);
    }
  }
}
