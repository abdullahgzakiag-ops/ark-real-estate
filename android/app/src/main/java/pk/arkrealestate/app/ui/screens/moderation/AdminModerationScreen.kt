package pk.arkrealestate.app.ui.screens.moderation

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
import pk.arkrealestate.app.data.model.MyListingDto
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.components.EmptyStateView
import pk.arkrealestate.app.ui.components.LoadingView
import pk.arkrealestate.app.ui.screens.marketplace.MarketplaceViewModel
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminModerationScreen(
    viewModel: MarketplaceViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.moderationState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingModerationQueue()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isUrdu) "ایڈمن ماڈریشن پورٹل" else "Admin Moderation Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ArkGoldAccent
                        ) {
                            Text(
                                text = "Rule 17 & 19",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ArkNavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingView(message = if (isUrdu) "زیر جائزہ لسٹنگز لوڈ ہو رہی ہیں..." else "Loading pending moderation queue...")
            } else if (state.pendingListings.isEmpty()) {
                EmptyStateView(
                    title = if (isUrdu) "کوئی لسٹنگ زیر جائزہ نہیں" else "Moderation Queue Clear",
                    message = if (isUrdu) "تمام جمع کرائی گئی پراپرٹیز کی جانچ پڑتال مکمل ہو چکی ہے۔" else "All submitted property listings have been verified and audited. Great job!",
                    actionText = if (isUrdu) "تازہ کریں" else "Refresh Queue",
                    onActionClick = { viewModel.loadPendingModerationQueue() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success banner
                    state.actionSuccessMessage?.let { msg ->
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ArkSuccessEmerald.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ArkSuccessEmerald)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = msg, style = MaterialTheme.typography.bodyMedium, color = ArkSuccessEmerald, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Header Overview
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = ArkNavyPrimary
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isUrdu) "کسٹوڈین تصدیقی قطار" else "Vetting Portal • Allotment Queue",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ArkGoldAccent
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ArkSurfaceWhite.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${state.pendingListings.size} Pending",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ArkSurfaceWhite,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (isUrdu)
                                        "پراپرٹی کو منظور کرنے پر ویریفائیڈ بیج خودکار طور پر جاری ہو جائے گا اور یہ فوری طور پر مارکیٹ پلیس سرچ میں دکھائی دے گی۔"
                                    else
                                        "Approving a listing grants the Verified Custodian badge and immediately makes it discoverable in public marketplace search queries.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ArkSurfaceWhite.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Items list
                    items(state.pendingListings, key = { it.id }) { listing ->
                        ModerationCard(
                            listing = listing,
                            isUrdu = isUrdu,
                            onApprove = { viewModel.approveListing(listing.id) },
                            onReject = { viewModel.openRejectModal(listing.id) }
                        )
                    }
                }
            }

            // Rejection Reason Modal
            if (state.isRejectModalOpen) {
                AlertDialog(
                    onDismissRequest = { viewModel.closeRejectModal() },
                    title = {
                        Text(
                            text = if (isUrdu) "لسٹنگ مسترد کرنے کی وجہ" else "Reject Listing / Request Re-audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = if (isUrdu)
                                    "برائے مہربانی وجہ بیان کریں تاکہ مالک / ایجنٹ ضروری دستاویزات دوبارہ فراہم کر سکے۔"
                                else
                                    "State the specific regulatory inconsistency or missing allotment documentation so the owner can amend it.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ArkTextSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            ArkTextField(
                                value = state.rejectionReason,
                                onValueChange = { viewModel.onRejectionReasonChanged(it) },
                                label = if (isUrdu) "وجہ / خامی" else "Rejection Reason",
                                placeholder = "e.g. Society NOC missing or allotment letter mismatch",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        ArkButton(
                            text = if (isUrdu) "مسترد کریں" else "Confirm Rejection",
                            onClick = { viewModel.rejectListing() },
                            variant = ArkButtonVariant.ACCENT,
                            isLoading = state.isProcessingAction
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.closeRejectModal() }) {
                            Text(text = if (isUrdu) "منسوخ" else "Cancel", color = ArkNavyPrimary)
                        }
                    },
                    containerColor = ArkSurfaceWhite,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ModerationCard(
    listing: MyListingDto,
    isUrdu: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Specs & Asking Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${listing.areaValue} ${listing.areaUnit.name} • ${listing.purpose.name}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArkGoldAccent
                )

                Text(
                    text = MarketplaceViewModel.formatPkrPrice(listing.pricePkr),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
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

            Spacer(modifier = Modifier.height(12.dp))

            // Audit Checklist Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = ArkCanvas
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isUrdu) "لازمی جانچ پڑتال" else "Custodian Audit Checklist",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ChecklistItem(text = "Allotment Letter / Title Deed Cross-Check")
                    ChecklistItem(text = "Society / Development Authority NOC Verification")
                    ChecklistItem(text = "Physical Demarcation & Specifications Alignment")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArkButton(
                    text = if (isUrdu) "مسترد کریں" else "Reject",
                    onClick = onReject,
                    variant = ArkButtonVariant.ACCENT,
                    modifier = Modifier.weight(1f)
                )

                ArkButton(
                    text = if (isUrdu) "تصدیق و منظوری" else "Verify & Approve",
                    onClick = onApprove,
                    variant = ArkButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}

@Composable
private fun ChecklistItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = ArkSuccessEmerald,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = ArkTextSecondary
        )
    }
}
