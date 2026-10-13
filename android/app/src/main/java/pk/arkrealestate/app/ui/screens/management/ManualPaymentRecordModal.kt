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
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.PaymentMethod
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.components.ManualPaymentDisclosureBadge
import pk.arkrealestate.app.ui.components.ValidationErrorBanner
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualPaymentRecordModal(
    uiState: ManualPaymentUiState,
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
                    text = "Record Rent Payment",
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

            Spacer(modifier = Modifier.height(14.dp))

            // Mandatory Rule 18 Compliance Disclosure
            ManualPaymentDisclosureBadge()

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                ValidationErrorBanner(message = uiState.errorMessage)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Input: Amount in PKR
            ArkTextField(
                value = uiState.amountPaidPkr,
                onValueChange = { viewModel.onPaymentAmountChanged(it) },
                label = "Amount Received (PKR)",
                placeholder = "e.g. 85000",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Selector
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = ArkNavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodChip(
                    text = "Bank Transfer",
                    isSelected = uiState.paymentMethod == PaymentMethod.BANK_TRANSFER,
                    onClick = { viewModel.onPaymentMethodChanged(PaymentMethod.BANK_TRANSFER) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    text = "Cash",
                    isSelected = uiState.paymentMethod == PaymentMethod.CASH,
                    onClick = { viewModel.onPaymentMethodChanged(PaymentMethod.CASH) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    text = "Cheque",
                    isSelected = uiState.paymentMethod == PaymentMethod.CHEQUE,
                    onClick = { viewModel.onPaymentMethodChanged(PaymentMethod.CHEQUE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Input: Bank Name
            if (uiState.paymentMethod == PaymentMethod.BANK_TRANSFER || uiState.paymentMethod == PaymentMethod.CHEQUE) {
                ArkTextField(
                    value = uiState.bankName,
                    onValueChange = { viewModel.onBankNameChanged(it) },
                    label = "Bank Name",
                    placeholder = "e.g. Meezan Bank, HBL, Allied Bank",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                ArkTextField(
                    value = uiState.referenceNumber,
                    onValueChange = { viewModel.onReferenceNumberChanged(it) },
                    label = "Transaction / Cheque Reference #",
                    placeholder = "e.g. FT-982410 or Cheque #004921",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Input: Notes
            ArkTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onPaymentNotesChanged(it) },
                label = "Remarks / Memo (Optional)",
                placeholder = "Rent for current month received against receipt #402",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            ArkButton(
                text = "Record Manual Payment",
                onClick = { viewModel.submitManualPayment() },
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
private fun PaymentMethodChip(
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
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ArkSurfaceWhite else ArkTextPrimary
            )
        }
    }
}
