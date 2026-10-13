package pk.arkrealestate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import pk.arkrealestate.app.data.repository.AuthRepository
import pk.arkrealestate.app.data.repository.ManagementRepository
import pk.arkrealestate.app.data.repository.MarketplaceRepository
import pk.arkrealestate.app.data.repository.TokenStorage
import pk.arkrealestate.app.ui.navigation.ArkNavGraph
import pk.arkrealestate.app.ui.navigation.Screen
import pk.arkrealestate.app.ui.screens.auth.AuthViewModel
import pk.arkrealestate.app.ui.screens.management.ManagementViewModel
import pk.arkrealestate.app.ui.screens.marketplace.MarketplaceViewModel
import pk.arkrealestate.app.ui.theme.ArkCanvas
import pk.arkrealestate.app.ui.theme.ArkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStorage = TokenStorage(applicationContext)
        val authRepository = AuthRepository(tokenStorage)
        val authViewModel = AuthViewModel(authRepository)

        val marketplaceRepository = MarketplaceRepository(tokenStorage)
        val marketplaceViewModel = MarketplaceViewModel(marketplaceRepository)

        val managementRepository = ManagementRepository(tokenStorage)
        val managementViewModel = ManagementViewModel(managementRepository)

        setContent {
            val sessionState by authViewModel.sessionState.collectAsState()
            val navController = rememberNavController()

            ArkTheme(isRtl = sessionState.isUrdu) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArkCanvas
                ) {
                    val startDestination = if (sessionState.isAuthenticated) {
                        Screen.Home.route
                    } else {
                        Screen.Welcome.route
                    }

                    ArkNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        marketplaceViewModel = marketplaceViewModel,
                        managementViewModel = managementViewModel,
                        isUrdu = sessionState.isUrdu,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
