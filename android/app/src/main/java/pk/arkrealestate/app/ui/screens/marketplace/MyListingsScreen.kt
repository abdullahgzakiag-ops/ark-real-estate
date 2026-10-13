package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import pk.arkrealestate.app.data.model.ListingStatus
import pk.arkrealestate.app.data.model.MyListingDto
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.BadgeStatus
import pk.arkrealestate.app.ui.components.EmptyStateView
import pk.arkrealestate.app.ui.components.LoadingView
import pk.arkrealestate.app.ui.components.StatusBadge
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    viewModel: MarketplaceViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToSubmit: () -> Unit,
    onListingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.myListingsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyListings()
    }

    val totalCount = state.listings.size
    val activeCount = state.listings.count { it.status == ListingStatus.APPROVED }
    val pendingCount = state.listings.count { it.status == ListingStatus.PENDING_APPROVAL }
    val rejectedCount = state.listings.count { it.status == ListingStatus.REJECTED }

    val filteredListings = remember(state.listings, state.selectedTab) {
        when (state.selectedTab) {
            "active" -> state.listings.filter { it.status == ListingStatus.APPROVED }
            "review" -> state.listings.filter { it.status == ListingStatus.PENDING_APPROVAL }
            "drafts" -> state.listings.filter { it.status == ListingStatus.REJECTED }
            else -> state.listings
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isUrdu) "میری لسٹنگز اور منظوری" else "My Listings & Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ArkNavyPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSubmit) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Listing", tint = ArkNavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArkSurfaceWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSubmit,
                containerColor = ArkGoldAccent,
                contentColor = ArkNavyPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "نئی لسٹنگ" else "Add Listing",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = ArkCanvas,
        modifier = modifier
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingView(message = if (isUrdu) "لسٹنگز لوڈ ہو رہی ہیں..." else "Loading your property listings...")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Regulatory Trust Banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = ArkSurfaceWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ArkNavyPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = ArkGoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUrdu) "کسٹوڈین پروٹوکول تحفظ" else "ARK Custodian Protocol",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ArkNavyPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isUrdu)
                                        "غیر تصدیق شدہ لسٹنگز عوام کے سامنے ظاہر نہیں ہوتیں جب تک کسٹوڈین ان کی توثیق نہ کرے۔"
                                    else
                                        "Unverified listings never appear in public marketplace search until approved by an ARK Legal Custodian.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ArkTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Metrics Ribbon
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricBox(
                            title = if (isUrdu) "کل لسٹنگز" else "Total",
                            value = totalCount.toString(),
                            color = ArkNavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = if (isUrdu) "فعال لسٹنگز" else "Active",
                            value = activeCount.toString(),
                            color = ArkSuccessEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = if (isUrdu) "زیر جائزہ" else "In Review",
                            value = pendingCount.toString(),
                            color = ArkWarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = if (isUrdu) "مسترد / ڈرافٹ" else "Rejected",
                            value = rejectedCount.toString(),
                            color = ArkErrorCrimson,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Filter Tabs
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            TabPill(
                                title = if (isUrdu) "سب" else "All ($totalCount)",
                                isSelected = state.selectedTab == "all",
                                onClick = { viewModel.onMyListingsTabSelected("all") }
                            )
                        }
                        item {
                            TabPill(
                                title = if (isUrdu) "فعال" else "Active ($activeCount)",
                                isSelected = state.selectedTab == "active",
                                onClick = { viewModel.onMyListingsTabSelected("active") }
                            )
                        }
                        item {
                            TabPill(
                                title = if (isUrdu) "زیر جائزہ" else "Under Review ($pendingCount)",
                                isSelected = state.selectedTab == "review",
                                onClick = { viewModel.onMyListingsTabSelected("review") }
                            )
                        }
                        item {
                            TabPill(
                                title = if (isUrdu) "مسترد" else "Rejected ($rejectedCount)",
                                isSelected = state.selectedTab == "drafts",
                                onClick = { viewModel.onMyListingsTabSelected("drafts") }
                            )
                        }
                    }
                }

                // Empty state or Listing Cards
                if (filteredListings.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = if (isUrdu) "کوئی لسٹنگ موجود نہیں" else "No Listings Found",
                            message = if (isUrdu) "آپ نے ابھی تک اس کیٹیگری میں کوئی پراپرٹی شامل نہیں کی۔" else "You haven't submitted any properties in this status category.",
                            actionText = if (isUrdu) "پراپرٹی جمع کرائیں" else "Submit a Property",
                            onActionClick = onNavigateToSubmit
                        )
                    }
                } else {
                    items(filteredListings, key = { it.id }) { listing ->
                        MyListingCard(
                            listing = listing,
                            isUrdu = isUrdu,
                            onClick = { onListingClick(listing.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ArkNavyPrimary else ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ArkNavyPrimary else ArkBorderSubtle)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) ArkSurfaceWhite else ArkNavyPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun MyListingCard(
    listing: MyListingDto,
    isUrdu: Boolean,
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
            // Status Badge & Purpose
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (listing.status) {
                    ListingStatus.APPROVED -> StatusBadge(status = BadgeStatus.ACTIVE, customText = if (isUrdu) "منظور شدہ" else "Live on Market")
                    ListingStatus.PENDING_APPROVAL -> StatusBadge(status = BadgeStatus.PENDING_REVIEW, customText = if (isUrdu) "زیر جائزہ" else "Pending Review")
                    ListingStatus.REJECTED -> StatusBadge(status = BadgeStatus.OVERDUE, customText = if (isUrdu) "مسترد شدہ" else "Rejected")
                    else -> StatusBadge(status = BadgeStatus.PENDING_REVIEW, customText = listing.status.name)
                }

                Text(
                    text = "${listing.areaValue} ${listing.areaUnit.name}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // Price PKR
            Text(
                text = MarketplaceViewModel.formatPkrPrice(listing.pricePkr),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ArkNavyPrimary
            )

            // Rejection reason if rejected
            if (listing.status == ListingStatus.REJECTED && !listing.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ArkErrorCrimson.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ArkErrorCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Note: ${listing.rejectionReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ArkErrorCrimson
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Counters Row (Inquiries, Views)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArkCanvas)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = ArkTextSecondary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${listing.inquiriesCount} Inquiries",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkNavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = ArkTextSecondary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${listing.viewingRequestsCount} Viewings",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkNavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
