package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.ListingPurpose
import pk.arkrealestate.app.ui.components.*
import pk.arkrealestate.app.ui.theme.*

@Composable
fun ListingDetailScreen(
    listingId: String,
    viewModel: MarketplaceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.detailState.collectAsState()
    val feedState by viewModel.feedState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(listingId) {
        viewModel.loadListingDetail(listingId)
    }

    val listing = uiState.listing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (uiState.isLoading) {
            LoadingStateView(message = "Loading property details...")
        } else if (listing == null) {
            OfflineStateView(onRetryClick = { viewModel.loadListingDetail(listingId) })
        } else {
            val isSaved = feedState.savedIds.contains(listing.id)
            val isRent = listing.purpose == ListingPurpose.FOR_RENT
            val formattedPrice = MarketplaceViewModel.formatPkrPrice(listing.pricePkr, isRent)

            Column(modifier = Modifier.fillMaxSize()) {
                // Top Custom App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ArkSurfaceWhite)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArkCanvas)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ArkNavyPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleSave(listing.id) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ArkCanvas)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isSaved) ArkGoldAccent else ArkNavyPrimary
                            )
                        }
                    }
                }

                // Scrollable Body Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Hero Media Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(ArkNavySecondary.copy(alpha = 0.15f))
                    ) {
                        // Purpose Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ArkNavyPrimary,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = if (listing.purpose == ListingPurpose.FOR_SALE) "FOR SALE" else "FOR RENT",
                                style = MaterialTheme.typography.labelMedium,
                                color = ArkGoldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        // Verified Badge
                        if (listing.isVerifiedBadge) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.BottomStart)
                            ) {
                                StatusBadge(status = BadgeStatus.VERIFIED)
                            }
                        }
                    }

                    // Main Info Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArkSurfaceWhite)
                            .padding(20.dp)
                    ) {
                        // Price
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ArkNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Title
                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ArkTextPrimary
                        )

                        if (!listing.titleUrdu.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = listing.titleUrdu,
                                style = MaterialTheme.typography.titleMedium,
                                color = ArkNavySecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Location
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ArkNavySecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${listing.areaLocation}, ${listing.city}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ArkTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specification Highlights Grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArkSurfaceWhite)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Property Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SpecCard(
                                icon = Icons.Default.SquareFoot,
                                label = "Area",
                                value = "${listing.areaValue} ${listing.areaUnit.name.replace("_", " ")}",
                                modifier = Modifier.weight(1f)
                            )
                            SpecCard(
                                icon = Icons.Default.KingBed,
                                label = "Bedrooms",
                                value = "${listing.bedrooms ?: 0} Beds",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SpecCard(
                                icon = Icons.Default.Bathtub,
                                label = "Bathrooms",
                                value = "${listing.bathrooms ?: 0} Baths",
                                modifier = Modifier.weight(1f)
                            )
                            SpecCard(
                                icon = Icons.Default.DirectionsCar,
                                label = "Parking",
                                value = "${listing.carSpaces ?: 0} Cars",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArkSurfaceWhite)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Property Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = listing.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ArkTextSecondary,
                            lineHeight = 22.sp
                        )

                        if (!listing.descriptionUrdu.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = ArkBorderSubtle)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = listing.descriptionUrdu,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ArkTextSecondary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Owner / Agent Profile Card
                    if (listing.owner != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ArkSurfaceWhite)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Listed By",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ArkNavyPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(ArkNavyPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = listing.owner.fullName.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ArkGoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = listing.owner.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ArkNavyPrimary
                                    )
                                    Text(
                                        text = listing.owner.agencyName ?: "Verified Estate Agent",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ArkTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                // Sticky Bottom Action Bar
                Surface(
                    color = ArkSurfaceWhite,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ArkButton(
                            text = "Inquire / Book Viewing",
                            onClick = { viewModel.openInquiryModal() },
                            variant = ArkButtonVariant.PRIMARY,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Inquiry / Viewing Modal BottomSheet
        if (uiState.isInquiryModalOpen) {
            InquiryModal(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.closeInquiryModal() }
            )
        }
    }
}

@Composable
private fun SpecCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ArkCanvas,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ArkNavySecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ArkTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ArkNavyPrimary
            )
        }
    }
}
