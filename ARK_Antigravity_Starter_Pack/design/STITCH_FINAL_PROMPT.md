# ARK Real Estate — Final Stitch Design Prompt

Design a polished, trustworthy, modern **native Android mobile app UI** named **ARK Real Estate** for Pakistan's property market. Generate connected mobile app screens and a reusable design system, not a marketing website.

## Product
ARK combines a property marketplace (buy/rent search, saved listings, inquiries and viewing requests) with property management (properties/units, tenants, leases, rent schedules, manual payment records, expenses, maintenance and reports).

## Visual style
Professional, calm, premium and trustworthy. Use deep navy/blue as primary, white and very light neutral surfaces, charcoal text, and a restrained teal or warm-gold accent. Use a clean sans-serif font, consistent spacing, clear hierarchy, rounded cards, restrained shadows and simple line icons. Maintain contrast and large touch targets. Avoid clutter, neon colors, excessive gradients and tiny text. Design Android portrait first, around 360–430 dp wide. Use fictional sample property listings and PKR prices.

## Localization
Show English UI examples and allow for Urdu translation. Include one Urdu/RTL example or variant; mirror directional layout appropriately. Do not create garbled Urdu text; use a labeled localization placeholder if accurate Urdu copy cannot be rendered.

## Roles and navigation
- Guest/buyer/renter: browse, search, filter, property details, save listings, inquiries, viewing requests.
- Owner/agent: submit property listings, track approval, manage authorized properties and inquiries.
- Property manager: properties/units, tenants, leases, rent schedules, manual payment records, expenses, maintenance and reports.
- Admin: approve/reject submitted listings before they become public.
Use consumer bottom navigation such as Home, Search, Saved, Inquiries, Profile. Show management tools only to authorized users. Admin review can be a separate role flow.

## Screens to generate
1. Splash/brand entry.
2. Welcome with Sign in/Create account.
3. Sign in.
4. Create account and appropriate role selection.
5. Home with search, Buy/Rent switch, categories, featured listings and “List a property”.
6. Search results with filters, sort and list/map toggle.
7. Filters: city/location, buy/rent, property type, PKR price range, bedrooms and bathrooms.
8. Property details with gallery, price, location, facts, description, save, inquiry and viewing actions.
9. Submit listing form with photo upload placeholders and clear pending-admin-review status.
10. Saved listings.
11. Inquiries/viewing requests with statuses.
12. Owner/agent dashboard with listing status and inquiry summaries.
13. Property management dashboard: properties, units, rent due, payment records, expenses, maintenance and reports.
14. Property/unit details.
15. Tenant details and lease.
16. Rent schedule with due/overdue/paid statuses and “Add payment record”. Make clear ARK records payments manually and does not transfer money.
17. Expenses with categories and PKR amounts.
18. Maintenance request list/detail.
19. Notifications.
20. Profile, settings, English/Urdu language selection and help.
21. Admin listing review with Approve/Reject and rejection reason.
22. Loading, empty, validation-error and network-error states.

## UX and safety
Use clear labels, useful validation, confirmation for destructive actions, and privacy-aware tenant/lease placeholders. Do not imply a listing is verified without a real process. Pending/rejected listings must not appear in public search. Distinguish “payment recorded” from “payment processed.” Use fictional examples in Lahore, Karachi, Islamabad and Rawalpindi.

## Deliverables
Create consistent connected screens and reusable styles/components for typography, spacing, colors, buttons, inputs, cards, status chips and navigation. Prioritize Home → Search Results → Property Details → Inquiry/Viewing Request and Sign in → Owner Dashboard → Submit Listing → Pending Approval. If all screens cannot be generated in one pass, first generate Home, Search Results, Property Details, Sign in and Owner Dashboard, then continue in separate prompts using the same design system. Do not generate backend code; implementation will happen separately in Antigravity.
