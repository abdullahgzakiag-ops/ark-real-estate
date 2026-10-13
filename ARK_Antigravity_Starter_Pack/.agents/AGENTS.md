# ARK Real Estate — Agent Instructions

Build ARK Real Estate in small, reviewable phases. Do not generate the whole app in one pass.

## Product decisions
- Market: Pakistan; currency: PKR.
- Android: Kotlin, Jetpack Compose, Navigation Compose, MVVM + Repository, Retrofit/OkHttp.
- Backend: Node.js, Express, TypeScript, REST API.
- Database: PostgreSQL + Prisma migrations.
- Images/private documents: cloud object storage.
- Languages: English and Urdu, including RTL.
- Rent payments in MVP are manual records only; ARK does not process or transfer money.
- Admin approval is required before every new listing is publicly visible.
- Marketplace: browse/search/filter/save listings, property details, inquiries, viewing requests, owner/agent listing submissions.
- Management: properties/units, tenants, leases, rent schedule, manual payment records, expenses, maintenance, reports.

## Agent rules
1. Inspect the actual repository before changing files. Never assume files exist.
2. Start with Phase 0 documentation only. Do not implement app features or migrations until the user approves the plan.
3. Before each phase, list goal, files to change, dependencies, and acceptance criteria.
4. Keep Android and API contracts consistent; implement one vertical feature at a time.
5. Do not silently change product decisions. Record significant proposals in `docs/DECISIONS.md` and ask approval.
6. Never claim tests/builds/migrations passed unless you actually ran them.
7. Run relevant tests, lint, type-checks and builds where available; report exact results.
8. Explain commands and ask before destructive operations or installing tools that need approval.
9. Never overwrite user work without inspecting it first.
10. Never commit secrets. Use `.env.example` placeholders only.
11. Hash passwords; never store plaintext passwords or return password hashes.
12. Enforce authorization on the backend for every protected record and admin action.
13. Validate all API inputs and uploaded file types/sizes. Keep tenant/lease documents private.
14. Use decimal/numeric money fields, never floating point.
15. Include loading, empty, success, validation-error and network-error UI states.
16. Keep the app accessible and support Urdu RTL.
17. Public search must exclude pending/rejected listings.
18. Clearly distinguish “payment recorded” from “payment processed.”
19. Do not claim a listing is verified unless a real verification process exists.

## Suggested repository
ARK-Real-Estate/
- `.agents/AGENTS.md`
- `.agents/workflows/ark-start.md`
- `android/`
- `backend/`
- `design/STITCH_FINAL_PROMPT.md`
- `docs/ARCHITECTURE.md`
- `docs/API_CONTRACT.md`
- `docs/DATABASE_SCHEMA.md`
- `docs/DECISIONS.md`
- `docs/IMPLEMENTATION_LOG.md`
- `README.md`
- `.gitignore`

## Build order
Phase 0: inspect repository, create structure and architecture/database/API/decision docs. No code features yet.
Phase 1: design Prisma schema, indexes/relations/constraints, review it, then create initial migration.
Phase 2: TypeScript/Express foundation, config validation, health endpoint, error handling, request validation, logging and tests.
Phase 3: authentication, password hashing, session/token strategy, rate limiting, current-user endpoint and role-based access.
Phase 4: Android Compose foundation, theme, navigation, networking, repositories, view models and authentication screens.
Phase 5: marketplace listing feed, filters, details, saved listings, inquiries/viewing requests, submissions and admin moderation.
Phase 6: properties/units, tenants, leases, rent schedules, manual payments, expenses, maintenance and reports.
Phase 7: English/Urdu localization, RTL, accessibility, integration tests, security review and release instructions.

## Phase completion report
Report what changed, files created, commands run and exact results, tests not run and why, blockers/decisions, and the single next step.

## First run
Do Phase 0 only. Inspect the actual folder, prepare the plan and docs, then stop and ask the user to approve Phase 1.
