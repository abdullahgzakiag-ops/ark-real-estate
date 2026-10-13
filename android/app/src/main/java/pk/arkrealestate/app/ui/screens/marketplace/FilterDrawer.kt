package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.ListingPurpose
import pk.arkrealestate.app.data.model.PropertyType
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDrawer(
    uiState: FeedUiState,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ArkCanvas,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ArkBorderSubtle)
            )
        },
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters & Sorting",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ArkTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Purpose (Buy vs Rent)
            Text(
                text = "Purpose",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ArkNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    text = "Any Purpose",
                    isSelected = uiState.selectedPurpose == null,
                    onClick = { viewModel.onPurposeSelected(null) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    text = "For Sale",
                    isSelected = uiState.selectedPurpose == ListingPurpose.FOR_SALE,
                    onClick = { viewModel.onPurposeSelected(ListingPurpose.FOR_SALE) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    text = "For Rent",
                    isSelected = uiState.selectedPurpose == ListingPurpose.FOR_RENT,
                    onClick = { viewModel.onPurposeSelected(ListingPurpose.FOR_RENT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Property Type
            Text(
                text = "Property Type",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ArkNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "All Types",
                        isSelected = uiState.selectedPropertyType == null,
                        onClick = { viewModel.onPropertyTypeSelected(null) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "House / Villa",
                        isSelected = uiState.selectedPropertyType == PropertyType.HOUSE,
                        onClick = { viewModel.onPropertyTypeSelected(PropertyType.HOUSE) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Apartment / Flat",
                        isSelected = uiState.selectedPropertyType == PropertyType.APARTMENT,
                        onClick = { viewModel.onPropertyTypeSelected(PropertyType.APARTMENT) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "Plot (Residential)",
                        isSelected = uiState.selectedPropertyType == PropertyType.RESIDENTIAL_PLOT,
                        onClick = { viewModel.onPropertyTypeSelected(PropertyType.RESIDENTIAL_PLOT) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Commercial",
                        isSelected = uiState.selectedPropertyType == PropertyType.COMMERCIAL_PLOT,
                        onClick = { viewModel.onPropertyTypeSelected(PropertyType.COMMERCIAL_PLOT) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "Office",
                        isSelected = uiState.selectedPropertyType == PropertyType.OFFICE,
                        onClick = { viewModel.onPropertyTypeSelected(PropertyType.OFFICE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bedrooms
            Text(
                text = "Bedrooms",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ArkNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "Any",
                    isSelected = uiState.selectedBedrooms == null,
                    onClick = { viewModel.onBedroomsSelected(null) },
                    modifier = Modifier.weight(1f)
                )
                listOf(1, 2, 3, 4, 5).forEach { bedCount ->
                    FilterChip(
                        text = "$bedCount+",
                        isSelected = uiState.selectedBedrooms == bedCount,
                        onClick = { viewModel.onBedroomsSelected(bedCount) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ArkButton(
                    text = "Reset All",
                    onClick = { viewModel.resetFilters() },
                    variant = ArkButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f)
                )

                ArkButton(
                    text = "Apply Filters",
                    onClick = { viewModel.applyFilters() },
                    variant = ArkButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ArkNavyPrimary else ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ArkNavyPrimary else ArkBorderSubtle
        ),
        modifier = modifier.height(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ArkSurfaceWhite else ArkTextPrimary
            )
        }
    }
}
