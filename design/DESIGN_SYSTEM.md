# ARK Real Estate — Design System & Stitch Export Specification

## 1. Design System Overview
- **Visual Style**: Corporate Modern with Refined Editorial Craft.
- **Brand Colors**:
  - Primary (`#0B1E36`): Deep Navy (top bars, prominent buttons, primary branding)
  - Secondary (`#1E3A5F`): Slate Navy (interactive tabs, secondary surfaces)
  - Tertiary Accent (`#C5A059`): Restrained Champagne Gold (verified badges, premium highlights)
  - Canvas Base (`#F8FAFC`): Soft Grayish-White
  - Card Surface (`#FFFFFF`): Pure White
  - Semantic Emerald (`#10B981`): Active, Verified, Paid
  - Semantic Amber (`#F59E0B`): Pending Review, Under Audit
  - Semantic Crimson (`#E11D48`): Overdue, Rejection, Errors
- **Typography**:
  - Primary font: **Plus Jakarta Sans** (weights 400, 500, 600, 700)
  - Urdu script font: **Noto Nastaliq Urdu** / **Noto Sans Arabic** (+20% line-height baseline compensation)
  - Currency display: Tabular numerals with explicit formatting (`PKR 4.5 Crore`, `PKR 85 Lac`, `PKR 45,000,000`)
- **Spacing Grid**: Strict 8dp native Android grid with 4dp micro-alignment subgrid. Minimum touch targets: `48dp x 48dp`.

---

## 2. Inventory of Exported Stitch Screens
All 21 Stitch screen folders in `stitch_ark_real_estate_mobile_app/` have been verified with complete UI code (`code.html`) and visual mockups (`screen.png`):

| Screen / Flow | Directory | Assets |
| :--- | :--- | :--- |
| Design System Tokens | `estate_trust_native/` | `DESIGN.md` |
| 1. Splash & Brand Entry | `estate_trust_native/` | `DESIGN.md` |
| 2. Welcome & Role Registration | `welcome_account_registration/` | `code.html`, `screen.png` |
| 3. Sign In | `sign_in_ark_real_estate/` | `code.html`, `screen.png` |
| 4. Create Account | `welcome_account_registration/` | `code.html`, `screen.png` |
| 5. Home Marketplace | `home_property_marketplace/` | `code.html`, `screen.png` |
| 6. Search Results & Toggle | `property_search_results/` | `code.html`, `screen.png` |
| 7. Filters Drawer | `property_search_filters/` | `code.html`, `screen.png` |
| 8. Property Details (1 Kanal Villa) | `property_details_1_kanal_villa/` | `code.html`, `screen.png` |
| 9. Submit Property Listing | `submit_property_listing/` | `code.html`, `screen.png` |
| 10. Saved Properties / Favourites | `saved_properties/` | `code.html`, `screen.png` |
| 11. Inquiries & Viewing Requests | `inquiries_viewing_requests/` | `code.html`, `screen.png` |
| 12. My Listings & Approval Status | `my_listings_approval_status/` | `code.html`, `screen.png` |
| 13. Owner / Management Dashboard | `owner_estate_management_dashboard/` | `code.html`, `screen.png` |
| 14. Property / Unit Details | `owner_estate_management_dashboard/` | `code.html`, `screen.png` |
| 15. Tenant & Lease Agreement | `tenant_lease_agreement/` | `code.html`, `screen.png` |
| 16. Rent Schedule & Manual Payment | `record_manual_rent_payment/` | `code.html`, `screen.png` |
| 17. Expenses & Financial Reports | `property_expenses_financial_reports/` | `code.html`, `screen.png` |
| 18. Maintenance & Repair Requests | `maintenance_repairs/` | `code.html`, `screen.png` |
| 19. Activity & Notification Alerts | `notifications_activity_alerts/` | `code.html`, `screen.png` |
| 20. Profile, Language & Settings | `profile_account_settings/` | `code.html`, `screen.png` |
| 21. Admin Listing Review | `admin_listing_review_verification/` | `code.html`, `screen.png` |
| 22. UI System & Network States | `ui_system_states_network_diagnostics/` | `code.html`, `screen.png` |
| RTL Urdu Variant | `ark_real_estate_rtl/` | `code.html`, `screen.png` |
