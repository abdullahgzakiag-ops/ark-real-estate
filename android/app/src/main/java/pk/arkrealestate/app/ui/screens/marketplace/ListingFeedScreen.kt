package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.ListingDto
import pk.arkrealestate.app.data.model.ListingPurpose
import pk.arkrealestate.app.ui.components.*
import pk.arkrealestate.app.ui.theme.*

@Composable
fun ListingFeedScreen(
    viewModel: MarketplaceViewModel,
    onListingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.feedState.collectAsState()
    val cities = listOf("All Cities", "Lahore", "Karachi", "Islamabad", "Rawalpindi", "Peshawar")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Search & Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArkSurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArkTextField(
                        value = uiState.searchQuery,
                        onValueChange = {
                            viewModel.onSearchQueryChanged(it)
                            viewModel.loadListings()
                        },
                        placeholder = "Search DHA, Bahria, Gulberg...",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Filter Button
                    Surface(
                        onClick = { viewModel.toggleFilterDrawer(true) },
                        shape = RoundedCornerShape(12.dp),
                        color = ArkNavyPrimary,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = ArkSurfaceWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // City Selector Chips Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cities) { city ->
                        val isSelected = uiState.selectedCity == city
                        Surface(
                            onClick = { viewModel.onCitySelected(city) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ArkNavyPrimary else ArkSurfaceContainerLow,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ArkNavyPrimary else ArkBorderSubtle
                            )
                        ) {
                            Text(
                                text = city,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ArkSurfaceWhite else ArkTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Body Content: Feed, Loading, or Empty States
            when {
                uiState.isLoading -> {
                    LoadingStateView(message = "Finding verified properties in Pakistan...")
                }
                uiState.errorMessage != null -> {
                    OfflineStateView(onRetryClick = { viewModel.loadListings() })
                }
                uiState.listings.isEmpty() -> {
                    EmptyStateView(
                        title = "No Properties Found",
                        description = "No active listings match your current search filters. Try selecting 'All Cities' or adjusting your criteria.",
                        onResetClick = { viewModel.resetFilters() }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.listings) { listing ->
                            PropertyCard(
                                listing = listing,
                                isSaved = uiState.savedIds.contains(listing.id),
                                onSaveToggle = { viewModel.toggleSave(listing.id) },
                                onClick = { onListingClick(listing.id) }
                            )
                        }
                    }
                }
            }
        }

        // Filter BottomSheet Drawer
        if (uiState.isFilterDrawerOpen) {
            FilterDrawer(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.toggleFilterDrawer(false) }
            )
        }
    }
}

@Composable
fun PropertyCard(
    listing: ListingDto,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Media Banner / Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(ArkNavySecondary.copy(alpha = 0.12f))
            ) {
                // Purpose Tag (For Sale / Rent)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ArkNavyPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (listing.purpose == ListingPurpose.FOR_SALE) "FOR SALE" else "FOR RENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkGoldAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Bookmark Icon Button
                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ArkSurfaceWhite.copy(alpha = 0.9f))
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Listing",
                        tint = if (isSaved) ArkGoldAccent else ArkNavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Verified Badge at Bottom Left of Image
                if (listing.isVerifiedBadge) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        StatusBadge(status = BadgeStatus.VERIFIED)
                    }
                }
            }

            // Card Body Details
            Column(modifier = Modifier.padding(16.dp)) {
                // Formatted Price in PKR
                val isRent = listing.purpose == ListingPurpose.FOR_RENT
                val formattedPrice = MarketplaceViewModel.formatPkrPrice(listing.pricePkr, isRent)

                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = ArkNavyPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Location with Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ArkTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${listing.areaLocation}, ${listing.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ArkTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ArkBorderSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Specification Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (listing.bedrooms != null && listing.bedrooms > 0) {
                        SpecItem(
                            icon = Icons.Default.KingBed,
                            label = "${listing.bedrooms} Beds"
                        )
                    }

                    if (listing.bathrooms != null && listing.bathrooms > 0) {
                        SpecItem(
                            icon = Icons.Default.Bathtub,
                            label = "${listing.bathrooms} Baths"
                        )
                    }

                    SpecItem(
                        icon = Icons.Default.SquareFoot,
                        label = "${listing.areaValue} ${listing.areaUnit.name.replace("_", " ")}"
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ArkNavySecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = ArkTextSecondary
        )
    }
}
