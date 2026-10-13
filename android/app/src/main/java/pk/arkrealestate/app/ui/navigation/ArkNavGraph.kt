package pk.arkrealestate.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pk.arkrealestate.app.ui.screens.auth.AuthViewModel
import pk.arkrealestate.app.ui.screens.auth.RegisterScreen
import pk.arkrealestate.app.ui.screens.auth.SignInScreen
import pk.arkrealestate.app.ui.screens.auth.WelcomeScreen
import pk.arkrealestate.app.ui.screens.diagnostics.DiagnosticsScreen
import pk.arkrealestate.app.ui.screens.home.HomeScreen
import pk.arkrealestate.app.ui.screens.management.FinancialReportScreen
import pk.arkrealestate.app.ui.screens.management.LeaseAgreementScreen
import pk.arkrealestate.app.ui.screens.management.ManagementViewModel
import pk.arkrealestate.app.ui.screens.management.PropertyPortfolioScreen
import pk.arkrealestate.app.ui.screens.marketplace.ListingDetailScreen
import pk.arkrealestate.app.ui.screens.marketplace.ListingFeedScreen
import pk.arkrealestate.app.ui.screens.marketplace.MarketplaceViewModel
import pk.arkrealestate.app.ui.screens.marketplace.MyListingsScreen
import pk.arkrealestate.app.ui.screens.marketplace.SavedListingsScreen
import pk.arkrealestate.app.ui.screens.marketplace.SubmitListingScreen
import pk.arkrealestate.app.ui.screens.moderation.AdminModerationScreen
import pk.arkrealestate.app.ui.screens.notifications.ActivityAlertsScreen
import pk.arkrealestate.app.ui.screens.profile.ProfileSettingsScreen

@Composable
fun ArkNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    managementViewModel: ManagementViewModel,
    isUrdu: Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                isUrdu = isUrdu,
                onToggleLanguage = { authViewModel.toggleLanguage() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToDiagnostics = { navController.navigate(Screen.Diagnostics.route) }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                viewModel = authViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                isUrdu = isUrdu,
                onToggleLanguage = { authViewModel.toggleLanguage() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = authViewModel,
                isUrdu = isUrdu,
                onNavigateToMarketplace = { navController.navigate(Screen.MarketplaceFeed.route) },
                onNavigateToSubmitListing = { navController.navigate(Screen.SubmitListing.route) },
                onNavigateToMyListings = { navController.navigate(Screen.MyListings.route) },
                onNavigateToSavedListings = { navController.navigate(Screen.SavedListings.route) },
                onNavigateToManagement = { navController.navigate(Screen.PropertyPortfolio.route) },
                onNavigateToAdminModeration = { navController.navigate(Screen.AdminModeration.route) },
                onNavigateToActivityAlerts = { navController.navigate(Screen.ActivityAlerts.route) },
                onNavigateToProfileSettings = { navController.navigate(Screen.ProfileSettings.route) },
                onNavigateToDiagnostics = { navController.navigate(Screen.Diagnostics.route) },
                onLoggedOut = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MarketplaceFeed.route) {
            ListingFeedScreen(
                viewModel = marketplaceViewModel,
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                }
            )
        }

        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ListingDetailScreen(
                listingId = listingId,
                viewModel = marketplaceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SubmitListing.route) {
            SubmitListingScreen(
                viewModel = marketplaceViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate(Screen.MyListings.route) {
                        popUpTo(Screen.SubmitListing.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MyListings.route) {
            MyListingsScreen(
                viewModel = marketplaceViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSubmit = { navController.navigate(Screen.SubmitListing.route) },
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                }
            )
        }

        composable(Screen.SavedListings.route) {
            SavedListingsScreen(
                viewModel = marketplaceViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onNavigateToMarketplace = { navController.navigate(Screen.MarketplaceFeed.route) }
            )
        }

        composable(Screen.AdminModeration.route) {
            AdminModerationScreen(
                viewModel = marketplaceViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PropertyPortfolio.route) {
            PropertyPortfolioScreen(
                viewModel = managementViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLease = { propertyId ->
                    navController.navigate(Screen.LeaseAgreement.createRoute(propertyId))
                },
                onNavigateToFinancialReport = { propertyId ->
                    navController.navigate(Screen.FinancialReport.createRoute(propertyId))
                }
            )
        }

        composable(
            route = Screen.LeaseAgreement.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            LeaseAgreementScreen(
                propertyId = propertyId,
                viewModel = managementViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FinancialReport.route,
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            FinancialReportScreen(
                propertyId = propertyId,
                viewModel = managementViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(
                viewModel = authViewModel,
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ActivityAlerts.route) {
            ActivityAlertsScreen(
                isUrdu = isUrdu,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
