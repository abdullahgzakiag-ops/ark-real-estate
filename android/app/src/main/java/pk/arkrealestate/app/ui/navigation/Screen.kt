package pk.arkrealestate.app.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object Register : Screen("register")
    object Home : Screen("home")
    object Diagnostics : Screen("diagnostics")
    object MarketplaceFeed : Screen("marketplace_feed")
    object ListingDetail : Screen("listing_detail/{listingId}") {
        fun createRoute(listingId: String) = "listing_detail/$listingId"
    }
    object PropertyPortfolio : Screen("property_portfolio")
    object SubmitListing : Screen("submit_listing")
    object MyListings : Screen("my_listings")
    object SavedListings : Screen("saved_listings")
    object AdminModeration : Screen("admin_moderation")
    object LeaseAgreement : Screen("lease_agreement/{propertyId}") {
        fun createRoute(propertyId: String) = "lease_agreement/$propertyId"
    }
    object FinancialReport : Screen("financial_report/{propertyId}") {
        fun createRoute(propertyId: String) = "financial_report/$propertyId"
    }
    object ProfileSettings : Screen("profile_settings")
    object ActivityAlerts : Screen("activity_alerts")
}

