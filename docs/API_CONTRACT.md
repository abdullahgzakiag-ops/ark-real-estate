# ARK Real Estate — REST API Contract

## 1. Global Standards & Conventions

- **Base URL**: `https://api.arkrealestate.pk/v1` (Production) / `http://localhost:5000/v1` (Local)
- **Data Format**: `application/json; charset=utf-8`
- **Authentication**: Bearer Token in `Authorization: Bearer <JWT_ACCESS_TOKEN>`
- **Response Format**: Uniform envelope for success and errors.
- **Monetary Units**: Pakistani Rupee (`PKR`), formatted in payloads as stringified exact decimals (e.g., `"45000000.00"`) to avoid floating-point loss in client JSON parsers.

### Standard Success Response
```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 142
  }
}
```

### Standard Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": [
      { "field": "pricePkr", "issue": "Price must be a positive number" }
    ]
  }
}
```

---

## 2. Authentication & Profile Endpoints

### `POST /auth/register`
Create a new user account with role selection.
- **Access**: Public
- **Request Body**:
```json
{
  "phone": "+923001234567",
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "fullName": "Ahmed Khan",
  "role": "OWNER_AGENT", // "BUYER_RENTER" | "OWNER_AGENT" | "PROPERTY_MANAGER"
  "agencyName": "Prime Properties Lahore", // optional
  "preferredLocale": "en" // "en" | "ur"
}
```
- **Response (201)**:
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid-1234",
      "fullName": "Ahmed Khan",
      "phone": "+923001234567",
      "email": "user@example.com",
      "role": "OWNER_AGENT",
      "preferredLocale": "en"
    },
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi..."
  }
}
```

### `POST /auth/login`
- **Access**: Public
- **Request Body**:
```json
{
  "phoneOrEmail": "+923001234567",
  "password": "SecurePassword123!"
}
```
- **Response (200)**: Token pair and user profile.

### `GET /auth/me`
- **Access**: Authenticated

---

## 3. Marketplace & Public Listing Endpoints

### `GET /listings`
Public search and browsing. **Crucial Rule: Strictly returns `status = 'APPROVED'` listings only.**
- **Access**: Public
- **Query Parameters**:
  - `purpose`: `FOR_SALE` | `FOR_RENT`
  - `propertyType`: `HOUSE` | `APARTMENT` | `COMMERCIAL_PLOT` | `RESIDENTIAL_PLOT`
  - `city`: e.g. `Lahore`, `Karachi`, `Islamabad`, `Rawalpindi`
  - `areaLocation`: e.g. `DHA Phase 5`, `Bahria Town`
  - `minPricePkr`: number
  - `maxPricePkr`: number
  - `bedrooms`: number
  - `bathrooms`: number
  - `page`: number (default: 1)
  - `limit`: number (default: 20)
  - `sort`: `NEWEST` | `PRICE_ASC` | `PRICE_DESC`
- **Response (200)**: Array of listing summary cards with primary image and localized labels.

### `GET /listings/:id`
Fetch complete listing detail with image gallery, seller badge, and unit specifications.
- **Access**: Public (if approved) / Owner & Admin (if pending/rejected)

### `POST /listings`
Submit a new property listing for administrative review.
- **Access**: Authenticated (`OWNER_AGENT`, `PROPERTY_MANAGER`, `ADMIN`)
- **Request Body**:
```json
{
  "title": "1 Kanal Luxury Contemporary Villa in DHA Phase 5",
  "titleUrdu": "ڈی ایچ اے فیز 5 میں 1 کنال کا لگژری ولا",
  "description": "Cantilevered architecture, Italian marble flooring, lush lawn.",
  "descriptionUrdu": "اطالوی ماربل، پرتعیش لان اور جدید ترین ڈیزائن۔",
  "purpose": "FOR_SALE",
  "propertyType": "HOUSE",
  "pricePkr": "85000000.00",
  "isNegotiable": true,
  "city": "Lahore",
  "areaLocation": "DHA Phase 5",
  "address": "Sector G, Street 12, DHA Phase 5",
  "areaValue": "1.00",
  "areaUnit": "KANAL",
  "bedrooms": 5,
  "bathrooms": 6,
  "carSpaces": 3,
  "imageStorageKeys": [
    "listings/uuid-img-1.jpg",
    "listings/uuid-img-2.jpg"
  ]
}
```
- **Response (201)**: Created listing with status `"PENDING_APPROVAL"`.

### `GET /listings/my`
Retrieve listings submitted by the authenticated user with approval statuses (`PENDING_APPROVAL`, `APPROVED`, `REJECTED`).

---

## 4. Admin Listing Moderation Endpoints

### `GET /admin/listings/pending`
Retrieve all pending listing submissions.
- **Access**: Admin only

### `PATCH /admin/listings/:id/review`
Approve or reject a listing.
- **Access**: Admin only
- **Request Body**:
```json
{
  "decision": "APPROVED", // "APPROVED" | "REJECTED"
  "rejectionReason": null // required if REJECTED
}
```
- **Response (200)**: Updated listing object with audit log entry created.

---

## 5. Inquiries & Viewing Requests

### `POST /listings/:id/inquiries`
Send a direct inquiry for a listing.
- **Access**: Authenticated

### `POST /listings/:id/viewings`
Book an in-person viewing inspection.
- **Access**: Authenticated
- **Request Body**:
```json
{
  "preferredDate": "2026-10-20T00:00:00Z",
  "preferredTime": "15:00",
  "contactPhone": "+923001234567",
  "notes": "Interested in viewing the master bedroom and rooftop terrace."
}
```

---

## 6. Property & Tenancy Management Endpoints

### `GET /management/properties`
List properties managed/owned by the user with occupancy and unit counts.
- **Access**: `OWNER_AGENT`, `PROPERTY_MANAGER`, `ADMIN`

### `POST /management/properties`
Register a managed property.

### `GET /management/units/:id/lease`
Retrieve active lease agreement, tenant details, and rent schedule.

### `POST /management/leases/:id/payments`
**Record a manual rent payment.** (ARK does not process money transactions).
- **Access**: Authorized Owner / Property Manager
- **Request Body**:
```json
{
  "rentScheduleId": "uuid-sched-1",
  "amountPaidPkr": "120000.00",
  "paymentDate": "2026-10-05T10:30:00Z",
  "paymentMethod": "BANK_TRANSFER", // "BANK_TRANSFER" | "CASH" | "CHEQUE" | "PAY_ORDER"
  "referenceNumber": "TRX-MEEZAN-9823145",
  "bankName": "Meezan Bank Limited",
  "receiptStorageKey": "receipts/slip_oct_2026.png",
  "notes": "Direct transfer confirmed via bank statement"
}
```
- **Response (201)**:
```json
{
  "success": true,
  "data": {
    "paymentRecord": {
      "id": "uuid-pay-88",
      "amountPaidPkr": "120000.00",
      "paymentMethod": "BANK_TRANSFER",
      "referenceNumber": "TRX-MEEZAN-9823145",
      "status": "RECORDED",
      "disclosure": "Payment logged manually. Subject to verification by ARK custodian before clearance."
    }
  }
}
```

### `GET /management/expenses` & `POST /management/expenses`
Track property maintenance, utilities, and tax expenditures in PKR.

### `GET /management/maintenance` & `POST /management/maintenance`
Log and update property maintenance and repair tickets.
