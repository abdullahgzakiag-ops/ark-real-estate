import express, { Express } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import path from 'path';
import { requestLogger } from './middlewares/requestLogger';
import { errorHandler, notFoundHandler } from './middlewares/errorHandler';
import { healthRoutes } from './routes/health.routes';
import { authRoutes } from './routes/auth.routes';
import listingRoutes from './routes/listing.routes';
import moderationRoutes from './routes/moderation.routes';
import propertyRoutes from './routes/property.routes';

export const createApp = (): Express => {
  const app = express();

  // Security middlewares (allow CDN fonts and styles for web portal)
  app.use(
    helmet({
      contentSecurityPolicy: false,
    })
  );
  app.use(
    cors({
      origin: true,
      credentials: true,
    })
  );

  // Parsing middlewares
  app.use(express.json({ limit: '10mb' }));
  app.use(express.urlencoded({ extended: true, limit: '10mb' }));

  // Logging
  app.use(requestLogger);

  // Static files for ARK Web Portal
  const publicDir = path.resolve(__dirname, '../public');
  app.use(express.static(publicDir));

  // API V1 Routes
  app.use('/v1/health', healthRoutes);
  app.use('/v1/auth', authRoutes);
  app.use('/v1/listings', listingRoutes);
  app.use('/v1/moderation', moderationRoutes);
  app.use('/v1/properties', propertyRoutes);

  // Fallback for web app pages (non-API routes)
  app.get('*', (req, res, next) => {
    if (req.path.startsWith('/v1/')) {
      return next();
    }
    const indexPath = path.join(publicDir, 'index.html');
    res.sendFile(indexPath, (err) => {
      if (err) {
        next();
      }
    });
  });

  // Fallback 404 for unknown API routes
  app.use(notFoundHandler);

  // Centralized Error Handler
  app.use(errorHandler);

  return app;
};

