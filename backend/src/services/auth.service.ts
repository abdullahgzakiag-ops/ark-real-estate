import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { UserRole, User } from '@prisma/client';
import { prisma } from '../config/prisma';
import { env } from '../config/env';
import { AppError } from '../utils/apiResponse';

export interface UserTokenPayload {
  id: string;
  role: UserRole;
  phone: string;
  email?: string | null;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export type SafeUser = Omit<User, 'passwordHash'>;

export class AuthService {
  /**
   * Securely hash a plaintext password using bcrypt with 10 salt rounds.
   */
  public static async hashPassword(password: string): Promise<string> {
    return bcrypt.hash(password, 10);
  }

  /**
   * Compare a plaintext password against a stored bcrypt hash.
   */
  public static async verifyPassword(password: string, hash: string): Promise<boolean> {
    return bcrypt.compare(password, hash);
  }

  /**
   * Normalize Pakistani phone number to standard E.164 format (+92XXXXXXXXXX).
   */
  public static normalizePhone(phone: string): string {
    const cleaned = phone.replace(/[\s\-()]/g, '');
    if (cleaned.startsWith('03')) {
      return '+92' + cleaned.substring(1);
    }
    if (cleaned.startsWith('923')) {
      return '+' + cleaned;
    }
    if (cleaned.startsWith('+923')) {
      return cleaned;
    }
    return cleaned;
  }

  /**
   * Generate an Access Token and Refresh Token pair for an authenticated user.
   */
  public static generateTokens(payload: UserTokenPayload): AuthTokens {
    const accessToken = jwt.sign(payload, env.JWT_SECRET, {
      expiresIn: env.JWT_ACCESS_EXPIRES_IN as jwt.SignOptions['expiresIn'],
    });

    const refreshToken = jwt.sign(payload, env.JWT_REFRESH_SECRET, {
      expiresIn: env.JWT_REFRESH_EXPIRES_IN as jwt.SignOptions['expiresIn'],
    });

    return { accessToken, refreshToken };
  }

  /**
   * Verify an Access Token and return the decoded token payload.
   */
  public static verifyAccessToken(token: string): UserTokenPayload {
    try {
      return jwt.verify(token, env.JWT_SECRET) as UserTokenPayload;
    } catch {
      throw new AppError('Invalid or expired authentication token', 401, 'UNAUTHORIZED');
    }
  }

  /**
   * Verify a Refresh Token and return the decoded payload.
   */
  public static verifyRefreshToken(token: string): UserTokenPayload {
    try {
      return jwt.verify(token, env.JWT_REFRESH_SECRET) as UserTokenPayload;
    } catch {
      throw new AppError('Invalid or expired refresh token', 401, 'INVALID_REFRESH_TOKEN');
    }
  }

  /**
   * Strip passwordHash and return a safe User DTO.
   */
  public static sanitizeUser(user: User): SafeUser {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { passwordHash, ...safeUser } = user;
    return safeUser;
  }

  /**
   * Register a new user with phone, password, role, and optional details.
   */
  public static async register(data: {
    phone: string;
    password: string;
    fullName: string;
    email?: string;
    role?: UserRole;
    agencyName?: string;
    preferredLocale?: string;
  }): Promise<{ user: SafeUser; tokens: AuthTokens }> {
    const normalizedPhone = this.normalizePhone(data.phone);

    // Check if phone is already registered
    const existingPhone = await prisma.user.findUnique({
      where: { phone: normalizedPhone },
    });
    if (existingPhone) {
      throw new AppError('An account with this phone number already exists', 409, 'PHONE_ALREADY_EXISTS');
    }

    // Check if email is already registered (if provided)
    if (data.email) {
      const existingEmail = await prisma.user.findUnique({
        where: { email: data.email.toLowerCase() },
      });
      if (existingEmail) {
        throw new AppError('An account with this email address already exists', 409, 'EMAIL_ALREADY_EXISTS');
      }
    }

    const passwordHash = await this.hashPassword(data.password);

    const user = await prisma.user.create({
      data: {
        phone: normalizedPhone,
        email: data.email ? data.email.toLowerCase() : null,
        fullName: data.fullName,
        passwordHash,
        role: data.role || UserRole.BUYER_RENTER,
        agencyName: data.agencyName || null,
        preferredLocale: data.preferredLocale || 'en',
      },
    });

    const tokens = this.generateTokens({
      id: user.id,
      role: user.role,
      phone: user.phone,
      email: user.email,
    });

    return {
      user: this.sanitizeUser(user),
      tokens,
    };
  }

  /**
   * Login user with phone or email and password.
   */
  public static async login(credentials: {
    phoneOrEmail: string;
    password: string;
  }): Promise<{ user: SafeUser; tokens: AuthTokens }> {
    const input = credentials.phoneOrEmail.trim();
    const normalizedPhone = this.normalizePhone(input);

    // Look up by normalized phone or lowercased email
    const user = await prisma.user.findFirst({
      where: {
        OR: [
          { phone: normalizedPhone },
          { email: input.toLowerCase() },
        ],
      },
    });

    if (!user) {
      throw new AppError('Invalid credentials', 401, 'INVALID_CREDENTIALS');
    }

    const isValidPassword = await this.verifyPassword(credentials.password, user.passwordHash);
    if (!isValidPassword) {
      throw new AppError('Invalid credentials', 401, 'INVALID_CREDENTIALS');
    }

    const tokens = this.generateTokens({
      id: user.id,
      role: user.role,
      phone: user.phone,
      email: user.email,
    });

    return {
      user: this.sanitizeUser(user),
      tokens,
    };
  }

  /**
   * Retrieve user profile by user ID.
   */
  public static async getUserById(userId: string): Promise<SafeUser> {
    const user = await prisma.user.findUnique({
      where: { id: userId },
    });

    if (!user) {
      throw new AppError('User profile not found', 404, 'USER_NOT_FOUND');
    }

    return this.sanitizeUser(user);
  }
}
