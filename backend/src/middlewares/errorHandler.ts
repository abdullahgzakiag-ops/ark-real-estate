import { Request, Response, NextFunction } from 'express';
import { ZodError } from 'zod';
import { AppError, sendError } from '../utils/apiResponse';

export const errorHandler = (
  err: Error,
  _req: Request,
  res: Response,
  _next: NextFunction
): void => {
  // Operational AppError with known status code
  if (err instanceof AppError) {
    sendError(res, err.message, err.statusCode, err.code, err.details);
    return;
  }

  // Zod validation error fallback
  if (err instanceof ZodError) {
    const details = err.issues.map((issue) => ({
      field: issue.path.join('.'),
      issue: issue.message,
    }));
    sendError(res, 'Validation failed', 422, 'VALIDATION_ERROR', details);
    return;
  }

  // JSON syntax error (e.g. malformed JSON in request body)
  if (err instanceof SyntaxError && 'status' in err && (err as { status: number }).status === 400) {
    sendError(res, 'Malformed JSON payload', 400, 'BAD_REQUEST');
    return;
  }

  // Unhandled internal server error
  console.error('💥 Unhandled Exception:', err);
  const isDev = process.env.NODE_ENV === 'development';
  sendError(
    res,
    isDev ? err.message : 'An unexpected internal server error occurred',
    500,
    'INTERNAL_SERVER_ERROR'
  );
};

export const notFoundHandler = (_req: Request, res: Response): void => {
  sendError(res, 'Resource not found', 404, 'NOT_FOUND');
};
