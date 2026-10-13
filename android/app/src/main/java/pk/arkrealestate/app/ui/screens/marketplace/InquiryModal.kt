package pk.arkrealestate.app.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkPhoneTextField
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryModal(
    uiState: DetailUiState,
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
                    text = "Contact Owner / Agent",
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

            if (uiState.actionSuccessMessage != null) {
                Surface(
                    color = ArkEmeraldBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ArkEmeraldSuccess
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.actionSuccessMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ArkEmeraldSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Input: Name
            ArkTextField(
                value = uiState.inquiryName,
                onValueChange = { viewModel.onInquiryNameChanged(it) },
                label = "Your Full Name",
                placeholder = "e.g. Tariq Mehmood",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input: Phone
            ArkPhoneTextField(
                value = uiState.inquiryPhone,
                onValueChange = { viewModel.onInquiryPhoneChanged(it) },
                label = "Contact Mobile Number",
                placeholder = "300 1234567",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input: Message
            ArkTextField(
                value = uiState.inquiryMessage,
                onValueChange = { viewModel.onInquiryMessageChanged(it) },
                label = "Inquiry Message",
                placeholder = "I am interested in this property. Is the price negotiable? When can I inspect the property?",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            ArkButton(
                text = "Send Direct Inquiry",
                onClick = { viewModel.submitInquiry() },
                variant = ArkButtonVariant.PRIMARY,
                isLoading = uiState.isSubmittingAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp)
            )
        }
    }
}
