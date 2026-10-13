package pk.arkrealestate.app.ui.screens.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.R
import pk.arkrealestate.app.ui.components.*
import pk.arkrealestate.app.ui.theme.*

@Composable
fun DiagnosticsScreen(
    isUrdu: Boolean,
    onToggleLanguage: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var samplePhone by remember { mutableStateOf("3001234567") }
    var showDismissableError by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArkSurfaceWhite)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ArkNavyPrimary
                )
            }

            Surface(
                onClick = onToggleLanguage,
                color = ArkSurfaceWhite,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Toggle Language",
                        tint = ArkGoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "English (LTR)" else "اردو (RTL)",
                        style = MaterialTheme.typography.labelMedium,
                        color = ArkNavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ARK Design System & Diagnostics Lab",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ArkNavyPrimary
        )

        Text(
            text = "Interactive verification of status badges, RTL flipping, resilient UI error states & regulatory disclosures.",
            style = MaterialTheme.typography.bodySmall,
            color = ArkTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // SECTION 1: BADGES & MODERATION
        SectionTitle(title = "1. Status Badges & Verification States")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge(status = BadgeStatus.VERIFIED)
            StatusBadge(status = BadgeStatus.ACTIVE)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge(status = BadgeStatus.PENDING_APPROVAL)
            StatusBadge(status = BadgeStatus.OVERDUE)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 2: REGULATORY COMPLIANCE DISCLOSURE
        SectionTitle(title = "2. Manual Rent Disclosure (Rule 18)")
        ManualPaymentDisclosureBadge()

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 3: FORM CONTROLS & BUTTONS
        SectionTitle(title = "3. Button Variants & Touch Targets")

        ArkButton(
            text = "Primary CTA Button (Navy 52dp)",
            onClick = {},
            variant = ArkButtonVariant.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArkButton(
            text = "Secondary Outlined Button",
            onClick = {},
            variant = ArkButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArkButton(
            text = "Gold Accent Action Button",
            onClick = {},
            variant = ArkButtonVariant.ACCENT,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        ArkButton(
            text = "Loading State Button",
            onClick = {},
            variant = ArkButtonVariant.PRIMARY,
            isLoading = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 4: RESILIENCE STATES (ERROR, EMPTY, OFFLINE)
        SectionTitle(title = "4. Resilient Empty & Network States")

        if (showDismissableError) {
            ValidationErrorBanner(
                message = "Validation Error: Mobile number must begin with 3xx (Pakistani standard).",
                onDismiss = { showDismissableError = false }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        EmptyStateView(
            title = stringResource(id = R.string.state_empty_title),
            description = stringResource(id = R.string.state_empty_desc),
            onResetClick = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        OfflineStateView(
            onRetryClick = {}
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = ArkNavySecondary,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
