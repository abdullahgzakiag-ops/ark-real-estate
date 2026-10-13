package pk.arkrealestate.app.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import pk.arkrealestate.app.data.model.RentPaymentStatus
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.BadgeStatus
import pk.arkrealestate.app.ui.components.StatusBadge
import pk.arkrealestate.app.ui.screens.marketplace.MarketplaceViewModel
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseAgreementScreen(
    propertyId: String,
    viewModel: ManagementViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val portfolioState by viewModel.portfolioState.collectAsState()
    val scrollState = rememberScrollState()

    val property = portfolioState.properties.find { it.id == propertyId } ?: portfolioState.properties.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isUrdu) "کرایہ داری کا معاہدہ" else "Tenancy & Lease Agreement",
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ArkSurfaceWhite)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tenant Hero Profile Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ArkNavyPrimary,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = ArkGoldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Muhammad Hamza Malik",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ArkNavyPrimary
                                )
                                Text(
                                    text = "Executive Tenant • Techlogix",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ArkTextSecondary
                                )
                            }
                        }

                        StatusBadge(status = BadgeStatus.ACTIVE, customText = "Active")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ArkCanvas)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ArkTextSecondary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "+92 321 8459201", style = MaterialTheme.typography.bodySmall, color = ArkNavyPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = ArkGoldAccent, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "NADRA Verified", style = MaterialTheme.typography.labelSmall, color = ArkNavyPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Leased Property & Terms Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "کرایہ داری کی شرائط" else "Tenancy Agreement Terms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArkNavyPrimary
                        )

                        Text(
                            text = "12 Months Term",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ArkGoldAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = property?.name ?: "Villa 42-B, Sector J",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )
                    Text(
                        text = property?.address ?: "DHA Phase 6, Lahore, Punjab",
                        style = MaterialTheme.typography.bodySmall,
                        color = ArkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rent & Deposit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = ArkCanvas
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = if (isUrdu) "ماہانہ کرایہ" else "Monthly Rent", style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "PKR 185,000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = ArkCanvas
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = if (isUrdu) "سیکیورٹی ڈپازٹ" else "Security Deposit", style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "PKR 370,000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
                            }
                        }
                    }
                }
            }

            // Rent Schedule Breakdown Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "ماہانہ ادائیگیوں کا ریکارڈ" else "Rent Schedules & Invoices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ScheduleRow(month = "October 2024", amount = "PKR 185,000", status = RentPaymentStatus.PAID)
                    Spacer(modifier = Modifier.height(10.dp))
                    ScheduleRow(month = "November 2024", amount = "PKR 185,000", status = RentPaymentStatus.PAID)
                    Spacer(modifier = Modifier.height(10.dp))
                    ScheduleRow(month = "December 2024", amount = "PKR 185,000", status = RentPaymentStatus.PENDING)
                    Spacer(modifier = Modifier.height(10.dp))
                    ScheduleRow(month = "January 2025", amount = "PKR 185,000", status = RentPaymentStatus.PENDING)
                }
            }

            // Quick Record Payment CTA (Rule 18 Audited Receipt)
            ArkButton(
                text = if (isUrdu) "دستی ادائیگی درج کریں (رول 18)" else "Log Manual Rent Payment (Rule 18)",
                onClick = {
                    val unitId = property?.units?.firstOrNull()?.id ?: "unit-1"
                    viewModel.openManualPaymentModal("lease-active-1")
                },
                variant = ArkButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ScheduleRow(
    month: String,
    amount: String,
    status: RentPaymentStatus
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ArkCanvas,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = month, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
                Text(text = amount, style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
            }

            when (status) {
                RentPaymentStatus.PAID -> StatusBadge(status = BadgeStatus.ACTIVE, customText = "Paid")
                RentPaymentStatus.PENDING -> StatusBadge(status = BadgeStatus.PENDING_REVIEW, customText = "Due")
                RentPaymentStatus.OVERDUE -> StatusBadge(status = BadgeStatus.OVERDUE, customText = "Overdue")
                RentPaymentStatus.PARTIAL -> StatusBadge(status = BadgeStatus.PENDING_REVIEW, customText = "Partial")
            }
        }
    }
}
