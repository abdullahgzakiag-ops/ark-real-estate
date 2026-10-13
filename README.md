# ARK Real Estate (آرک رئیل اسٹیٹ)

> A modern, premium property marketplace and property management platform designed for Pakistan.

ARK Real Estate combines an administrative-moderated property marketplace (buy, rent, search, filter, saved favourites, inquiries, and viewing requests) with property management (properties, units, tenants, leases, rent schedules, manual payment records, expenses, maintenance, and yield reports).

---

## 1. Key System Architectural Decisions

1. **Market & Currency**: Pakistan primary (`PKR`) with exact `Decimal` precision (Rule 14). Tabular pricing in Crores, Lakhs, and Thousands.
2. **Administrative Moderation**: Every newly submitted listing requires admin review before appearing in public searches (`status: APPROVED`) (Rule 17 & 19).
3. **Manual Rent Records (Audited Ledger)**: ARK does not initiate or transfer funds. All rent logs are audited manual records with the mandatory compliance disclosure (Rule 18).
4. **Bilingual & Bidirectional**: English (`en-PK`) and Urdu (`ur-PK`) with dynamic LTR/RTL layout direction switching.
5. **Security & RBAC**: Passwords hashed with bcrypt; JWT Access (15m) + Refresh (7d) tokens; RBAC enforcing permissions for `BUYER_RENTER`, `OWNER_AGENT`, `PROPERTY_MANAGER`, and `ADMIN`.

---

## 2. Technology Stack

- **Android Application**:
  - Language: Kotlin
  - UI Toolkit: Jetpack Compose (BOM `2024.04.01`), Material 3, Navigation Compose
  - Architecture: MVVM + Repository Pattern, StateFlow, Coroutines
  - Networking: Retrofit `2.11.0`, OkHttp `4.12.0`, Gson Converter
- **Backend Service**:
  - Runtime: Node.js (`v20+` / `v24`), Express.js, TypeScript
  - Validation: Zod runtime schema validation
  - Database: PostgreSQL with Prisma ORM
  - Security: Helmet, CORS, bcryptjs, jsonwebtoken, express-rate-limit

---

## 3. Repository Structure

```
d:/stitch_ark_real_estate_mobile_app/
├── android/                        # Android Native Jetpack Compose App
│   ├── app/src/main/
│   │   ├── AndroidManifest.xml     # Network permissions, RTL support
│   │   ├── java/pk/arkrealestate/app/
│   │   │   ├── data/               # Models, Retrofit service, Repositories
│   │   │   ├── ui/
│   │   │   │   ├── components/     # ArkButton, ArkTextField, StatusBadge, StateViews
│   │   │   │   ├── navigation/     # Screen routes, ArkNavGraph
│   │   │   │   ├── screens/        # Auth, Diagnostics, Home, Marketplace, Management
│   │   │   │   └── theme/          # Color tokens, Typography, Shapes, Dynamic ArkTheme
│   │   │   ├── ArkApplication.kt
│   │   │   └── MainActivity.kt     # App entry point
│   │   └── res/
│   │       ├── values/strings.xml  # English strings catalog
│   │       └── values-ur/strings.xml # Urdu localized string catalog
├── backend/                        # Node.js + Express + TypeScript API
│   ├── prisma/
│   │   ├── schema.prisma           # 14 Prisma models (PostgreSQL)
│   │   └── migrations/0_init/      # Baseline SQL migration
│   ├── src/
│   │   ├── config/                 # Env validation (Zod), Prisma client
│   │   ├── controllers/            # Auth, Listing, Moderation, Property controllers
│   │   ├── middlewares/            # Auth JWT, RBAC, Error boundary, Request logger
│   │   ├── routes/                 # Health, Auth, Listing, Moderation, Property routes
│   │   ├── services/               # Business logic (Auth, Listing, Property)
│   │   ├── utils/                  # Standardized API response envelope
│   │   ├── app.ts                  # Express application factory
│   │   └── index.ts                # HTTP server launcher
│   └── tests/                      # Jest test suites (Health, Middleware, Auth, Listing, Property)
├── docs/                           # Architecture, DB Schema, API Contract, Decisions, Log
└── stitch_ark_real_estate_mobile_app/ # 21 exported Stitch UI designs (HTML & PNG)
```

---

## 4. Getting Started & Local Development

### Prerequisites
- Node.js `v20.x` or later
- PostgreSQL database instance
- Android Studio Iguana / Jellyfish (for Android build/emulator)

### Backend Setup
1. Navigate to backend directory:
   ```bash
   cd backend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure environment variables:
   ```bash
   cp .env.example .env
   # Edit .env with your PostgreSQL DATABASE_URL and JWT secrets
   ```
4. Run Prisma schema validation and generate client:
   ```bash
   npx prisma validate
   npx prisma generate
   ```
5. Run database migrations:
   ```bash
   npx prisma migrate dev
   ```
6. Build and start development server:
   ```bash
   npm run build
   npm run dev
   # Server runs on http://localhost:5000
   ```

### Running Backend Tests
```bash
npm test
# Runs Jest test suites across Auth, Health, Middlewares, Marketplace, and Property Management
```

### Android Setup
1. Open the `android/` directory in Android Studio.
2. Allow Gradle sync to complete using the Version Catalog (`gradle/libs.versions.toml`).
3. Run on an Android Emulator (API 26+) or connected physical device.
   - Note: In Android Emulator, `10.0.2.2:5000` automatically maps to `localhost:5000` of the host computer.

---

## 5. Security & Legal Audit Verification

| Rule | Requirement | Implementation | Status |
| :--- | :--- | :--- | :--- |
| **Rule 10** | Never commit secrets | `.env` ignored in `.gitignore`, `.env.example` has placeholders only | ✅ Verified |
| **Rule 11** | Password hashing | Hashed with bcrypt (10 rounds); `sanitizeUser` removes `passwordHash` | ✅ Verified |
| **Rule 12** | Authorization on every record | `authenticate` Bearer JWT + `requireRole` RBAC on all protected endpoints | ✅ Verified |
| **Rule 13** | Input validation | Strict Zod runtime schemas on all incoming requests | ✅ Verified |
| **Rule 14** | Decimal money precision | `Decimal(14, 2)` used everywhere; floating point numbers forbidden | ✅ Verified |
| **Rule 17** | Exclude pending from search | Public `getListings` strictly enforces `status = APPROVED` | ✅ Verified |
| **Rule 18** | Manual payment disclosure | Every manual payment includes prominent disclosure; no money processing | ✅ Verified |
| **Rule 19** | Verification process | `isVerifiedBadge` granted strictly after administrative moderation approval | ✅ Verified |

---

## 6. Development Progress Summary

All 8 Phases (Phase 0 through Phase 7) have been designed, implemented, and verified according to project instructions and architectural contracts. Details are documented in [docs/IMPLEMENTATION_LOG.md](file:///d:/stitch_ark_real_estate_mobile_app/docs/IMPLEMENTATION_LOG.md).
