package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.ListingDto
import pk.arkrealestate.app.ui.components.EmptyStateView
import pk.arkrealestate.app.ui.components.LoadingView
import pk.arkrealestate.app.ui.components.StatusBadge
import pk.arkrealestate.app.ui.components.BadgeStatus
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListingsScreen(
    viewModel: MarketplaceViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    onListingClick: (String) -> Unit,
    onNavigateToMarketplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.savedListingsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSavedListings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isUrdu) "محفوظ شدہ پراپرٹیز" else "Saved Properties",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )
                        if (state.listings.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ArkGoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${state.listings.size} Saved",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ArkNavyPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ArkNavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArkSurfaceWhite)
            )
        },
        containerColor = ArkCanvas,
        modifier = modifier
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingView(message = if (isUrdu) "محفوظ پراپرٹیز لوڈ ہو رہی ہیں..." else "Loading saved properties...")
        } else if (state.listings.isEmpty()) {
            EmptyStateView(
                title = if (isUrdu) "کوئی محفوظ پراپرٹی نہیں" else "No Saved Properties",
                message = if (isUrdu) "آپ نے ابھی تک کوئی پراپرٹی محفوظ نہیں کی۔ مارکیٹ پلیس پر دل کے نشان کو دبائیں تاکہ بعد میں دیکھ سکیں۔" else "You haven't bookmarked any listings yet. Tap the heart icon on any property to save it here.",
                actionText = if (isUrdu) "مارکیٹ پلیس تلاش کریں" else "Explore Marketplace",
                onActionClick = onNavigateToMarketplace,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.listings, key = { it.id }) { listing ->
                    SavedListingCard(
                        listing = listing,
                        onUnsave = { viewModel.removeSavedListing(listing.id) },
                        onClick = { onListingClick(listing.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedListingCard(
    listing: ListingDto,
    onUnsave: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Price and Unsave Heart Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MarketplaceViewModel.formatPkrPrice(listing.pricePkr),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArkNavyPrimary
                )

                IconButton(onClick = onUnsave) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from saved",
                        tint = ArkErrorCrimson
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ArkNavyPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ArkTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${listing.areaLocation}, ${listing.city}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArkCanvas)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${listing.areaValue} ${listing.areaUnit.name}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )

                listing.bedrooms?.let { beds ->
                    Text(
                        text = "$beds Beds",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkTextSecondary
                    )
                }

                listing.bathrooms?.let { baths ->
                    Text(
                        text = "$baths Baths",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkTextSecondary
                    )
                }

                if (listing.isVerifiedBadge) {
                    StatusBadge(status = BadgeStatus.VERIFIED, customText = "Verified")
                }
            }
        }
    }
}
