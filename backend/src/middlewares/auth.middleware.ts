import { Request, Response, NextFunction } from 'express';
import { UserRole } from '@prisma/client';
import { AuthService, UserTokenPayload } from '../services/auth.service';
import { sendError, AppError } from '../utils/apiResponse';

// Augment Express Request interface to include authenticated user payload
declare global {
  namespace Express {
    interface Request {
      user?: UserTokenPayload;
    }
  }
}

/**
 * Middleware to authenticate requests via JWT Bearer token.
 */
export const authenticate = (req: Request, res: Response, next: NextFunction): void => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    sendError(res, 'Authentication token required', 401, 'UNAUTHORIZED');
    return;
  }

  const token = authHeader.split(' ')[1];
  try {
    const payload = AuthService.verifyAccessToken(token);
    req.user = payload;
    next();
  } catch (error) {
    if (error instanceof AppError) {
      sendError(res, error.message, error.statusCode, error.code);
      return;
    }
    sendError(res, 'Invalid authentication token', 401, 'UNAUTHORIZED');
  }
};

/**
 * Middleware to enforce Role-Based Access Control (RBAC).
 */
export const requireRole = (allowedRoles: UserRole[]) => {
  return (req: Request, res: Response, next: NextFunction): void => {
    if (!req.user) {
      sendError(res, 'Authentication required', 401, 'UNAUTHORIZED');
      return;
    }

    if (!allowedRoles.includes(req.user.role)) {
      sendError(
        res,
        'Access denied: You do not have permission to perform this action',
        403,
        'FORBIDDEN'
      );
      return;
    }

    next();
  };
};
