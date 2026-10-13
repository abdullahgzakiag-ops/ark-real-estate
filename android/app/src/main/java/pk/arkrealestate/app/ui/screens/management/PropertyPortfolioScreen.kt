package pk.arkrealestate.app.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.PropertyDto
import pk.arkrealestate.app.data.model.UnitDto
import pk.arkrealestate.app.ui.components.*
import pk.arkrealestate.app.ui.screens.marketplace.MarketplaceViewModel
import pk.arkrealestate.app.ui.theme.*

@Composable
fun PropertyPortfolioScreen(
    viewModel: ManagementViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLease: (String) -> Unit = {},
    onNavigateToFinancialReport: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.portfolioState.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()
    val maintenanceState by viewModel.maintenanceState.collectAsState()


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArkSurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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

                Text(
                    text = "Property Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )

                Spacer(modifier = Modifier.width(40.dp))
            }

            if (uiState.isLoading) {
                LoadingStateView(message = "Loading property portfolios...")
            } else if (uiState.errorMessage != null) {
                OfflineStateView(onRetryClick = { viewModel.loadProperties() })
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Portfolio KPI Card
                    item {
                        PortfolioKpiCard(
                            totalUnits = uiState.totalPortfolioUnits,
                            occupiedUnits = uiState.totalOccupiedUnits,
                            occupancyRate = uiState.overallOccupancyRate,
                            totalRentRollPkr = uiState.totalRentRollPkr
                        )
                    }

                    // Properties List
                    items(uiState.properties) { property ->
                        PropertyPortfolioCard(
                            property = property,
                            onRecordPayment = { unit ->
                                viewModel.openManualPaymentModal(
                                    leaseId = "sample-lease-id",
                                    suggestedAmount = unit.monthlyRentPkr
                                )
                            },
                            onReportMaintenance = { unit ->
                                viewModel.openMaintenanceModal(unit.id)
                            },
                            onViewLease = { onNavigateToLease(property.id) },
                            onViewFinancialReport = { onNavigateToFinancialReport(property.id) }
                        )
                    }
                }
            }
        }

        // Modals
        if (paymentState.isModalOpen) {
            ManualPaymentRecordModal(
                uiState = paymentState,
                viewModel = viewModel,
                onDismiss = { viewModel.closeManualPaymentModal() }
            )
        }

        if (maintenanceState.isModalOpen) {
            MaintenanceRequestModal(
                uiState = maintenanceState,
                viewModel = viewModel,
                onDismiss = { viewModel.closeMaintenanceModal() }
            )
        }
    }
}

@Composable
private fun PortfolioKpiCard(
    totalUnits: Int,
    occupiedUnits: Int,
    occupancyRate: Int,
    totalRentRollPkr: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ArkNavyPrimary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "OVERALL PORTFOLIO HEALTH",
                style = MaterialTheme.typography.labelSmall,
                color = ArkGoldAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$occupancyRate% Occupancy",
                        style = MaterialTheme.typography.headlineSmall,
                        color = ArkSurfaceWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$occupiedUnits of $totalUnits Units Occupied",
                        style = MaterialTheme.typography.bodySmall,
                        color = ArkSurfaceWhite.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    color = ArkSurfaceWhite.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Est. Monthly Roll",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArkSurfaceWhite.copy(alpha = 0.8f)
                        )
                        Text(
                            text = MarketplaceViewModel.formatPkrPrice(totalRentRollPkr, true),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ArkGoldAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyPortfolioCard(
    property: PropertyDto,
    onRecordPayment: (UnitDto) -> Unit,
    onReportMaintenance: (UnitDto) -> Unit,
    onViewLease: () -> Unit,
    onViewFinancialReport: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )

                Surface(
                    color = ArkEmeraldBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${property.occupiedUnits}/${property.totalUnits} Units",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArkEmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ArkTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${property.areaLocation}, ${property.city}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: View Lease & Financial Report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArkButton(
                    text = "Tenancy Leases",
                    onClick = onViewLease,
                    variant = ArkButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
                ArkButton(
                    text = "Yield Report",
                    onClick = onViewFinancialReport,
                    variant = ArkButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f)
                )
            }

            if (property.units.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ArkBorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Units & Tenancies",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkNavySecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                property.units.forEach { unit ->
                    UnitRowItem(
                        unit = unit,
                        onRecordPayment = { onRecordPayment(unit) },
                        onReportMaintenance = { onReportMaintenance(unit) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
private fun UnitRowItem(
    unit: UnitDto,
    onRecordPayment: () -> Unit,
    onReportMaintenance: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ArkCanvas,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = unit.unitNumber,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = MarketplaceViewModel.formatPkrPrice(unit.monthlyRentPkr, true),
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onReportMaintenance,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArkSurfaceWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Maintenance",
                        tint = ArkNavySecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onRecordPayment,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArkNavyPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Record Payment",
                        tint = ArkSurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
