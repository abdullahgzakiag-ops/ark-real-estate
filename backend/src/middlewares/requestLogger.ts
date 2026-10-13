import { Request, Response, NextFunction } from 'express';

export const requestLogger = (req: Request, res: Response, next: NextFunction): void => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    const status = res.statusCode;
    const color =
      status >= 500
        ? '\x1b[31m' // Red
        : status >= 400
        ? '\x1b[33m' // Yellow
        : status >= 300
        ? '\x1b[36m' // Cyan
        : '\x1b[32m'; // Green
    const reset = '\x1b[0m';

    // Only log in non-test environments
    if (process.env.NODE_ENV !== 'test') {
      console.log(
        `[${new Date().toISOString()}] ${req.method} ${req.originalUrl} ${color}${status}${reset} - ${duration}ms`
      );
    }
  });
  next();
};
