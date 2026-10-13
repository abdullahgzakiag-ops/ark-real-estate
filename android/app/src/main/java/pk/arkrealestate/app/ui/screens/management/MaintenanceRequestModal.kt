package pk.arkrealestate.app.ui.screens.management

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pk.arkrealestate.app.data.model.MaintenancePriority
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.components.ValidationErrorBanner
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceRequestModal(
    uiState: MaintenanceUiState,
    viewModel: ManagementViewModel,
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
                    text = "Report Maintenance Issue",
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

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                ValidationErrorBanner(message = uiState.errorMessage)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Priority Selector
            Text(
                text = "Issue Priority",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ArkNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip(
                    text = "Low",
                    isSelected = uiState.priority == MaintenancePriority.LOW,
                    onClick = { viewModel.onMaintenancePriorityChanged(MaintenancePriority.LOW) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    text = "Medium",
                    isSelected = uiState.priority == MaintenancePriority.MEDIUM,
                    onClick = { viewModel.onMaintenancePriorityChanged(MaintenancePriority.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    text = "High",
                    isSelected = uiState.priority == MaintenancePriority.HIGH,
                    onClick = { viewModel.onMaintenancePriorityChanged(MaintenancePriority.HIGH) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    text = "Urgent",
                    isSelected = uiState.priority == MaintenancePriority.URGENT,
                    onClick = { viewModel.onMaintenancePriorityChanged(MaintenancePriority.URGENT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Issue Title
            ArkTextField(
                value = uiState.title,
                onValueChange = { viewModel.onMaintenanceTitleChanged(it) },
                label = "Issue Title",
                placeholder = "e.g. Water pump malfunction, Electrical short circuit",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Issue Description
            ArkTextField(
                value = uiState.description,
                onValueChange = { viewModel.onMaintenanceDescriptionChanged(it) },
                label = "Detailed Description",
                placeholder = "Please explain the defect, location in the unit, and urgency...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            ArkButton(
                text = "Submit Maintenance Ticket",
                onClick = { viewModel.submitMaintenanceRequest() },
                variant = ArkButtonVariant.PRIMARY,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            )
        }
    }
}

@Composable
private fun PriorityChip(
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
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ArkSurfaceWhite else ArkTextPrimary
            )
        }
    }
}
