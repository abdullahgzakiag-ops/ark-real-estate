# ARK Real Estate — Architecture Decision Records (ADR)

## ADR-001: Mobile Platform & UI Framework Selection
- **Status**: Accepted
- **Decision**: Use native **Kotlin with Jetpack Compose** and **Navigation Compose** for Android.
- **Context**: Real estate applications demand fluid image galleries, smooth pinch-to-zoom floor plans, native map rendering, and robust hardware acceleration. Jetpack Compose enables reactive state management, seamless RTL mirroring for Urdu, and direct alignment with Google Stitch design token exports.

---

## ADR-002: Backend Runtime & Framework
- **Status**: Accepted
- **Decision**: **Node.js LTS with Express.js and TypeScript**.
- **Context**: TypeScript delivers strong static typing across the data layer, API controllers, and request validation schemas. Node.js provides lightweight, non-blocking I/O ideal for media uploads, search filtering, and concurrent tenant notifications.

---

## ADR-003: Database Engine & ORM
- **Status**: Accepted
- **Decision**: **PostgreSQL** with **Prisma ORM**.
- **Context**: Real estate data is inherently relational: properties link to units, leases, tenants, rent schedules, and expenses. PostgreSQL guarantees ACID compliance, robust indexing on geographic coordinates and text, and strict Decimal types for currency. Prisma provides compile-time type safety and automated migration tracking.

---

## ADR-004: Manual Rent Payment Recording (No Payment Gateways in MVP)
- **Status**: Accepted
- **Decision**: ARK will **never initiate, process, or transfer money** in the MVP.
- **Context**: Property rent transactions in Pakistan predominantly take place via direct online bank transfers (IBFT), Raast, crossed cheques, or physical cash receipts. Integrating third-party payment gateways for large tenancy payments introduces regulatory escrow and banking compliance overhead. ARK serves strictly as a verified ledger. Payments are labeled as "Recorded / Logged Manually" and require manual verification.

---

## ADR-005: Mandatory Administrative Listing Review Workflow
- **Status**: Accepted
- **Decision**: All newly submitted or modified property listings enter `PENDING_APPROVAL` status and are excluded from public search queries until reviewed and approved by an Administrator.
- **Context**: The Pakistan property market suffers from fake listings, duplicate listings, and inaccurate pricing. Mandatory human moderation safeguards marketplace trust and prevents spam.

---

## ADR-006: Separation of Cloud Object Storage vs Database
- **Status**: Accepted
- **Decision**: Property image galleries and private documents are stored in cloud object storage (S3/GCS compatible). The database stores only object keys and URLs.
- **Context**: Storing binary data in PostgreSQL bloats backups and degrades query performance. Private documents (CNIC cards, tenancy agreements) use private storage buckets with time-limited presigned URLs.

---

## ADR-007: Bilingual & RTL Support Strategy
- **Status**: Accepted
- **Decision**: Support English (`en-PK`) and Urdu (`ur-PK`) natively, with automatic RTL layout mirroring in Compose.
- **Context**: English is the commercial standard in Pakistan, while Urdu is essential for widespread accessibility. Arabic script requires baseline line-height adjustments (+20%) and mirrored layouts for intuitive RTL navigation.
