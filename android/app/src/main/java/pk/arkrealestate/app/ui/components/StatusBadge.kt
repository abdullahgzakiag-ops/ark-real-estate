package pk.arkrealestate.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pk.arkrealestate.app.ui.theme.*

enum class BadgeStatus {
    VERIFIED,
    PENDING_APPROVAL,
    ACTIVE,
    OVERDUE
}

@Composable
fun StatusBadge(
    status: BadgeStatus,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val (bgColor, textColor, icon) = when (status) {
        BadgeStatus.VERIFIED -> Triple(ArkEmeraldBg, ArkEmeraldSuccess, Icons.Filled.Verified)
        BadgeStatus.PENDING_APPROVAL -> Triple(ArkAmberBg, ArkAmberWarning, Icons.Filled.Schedule)
        BadgeStatus.ACTIVE -> Triple(ArkEmeraldBg, ArkEmeraldSuccess, Icons.Filled.CheckCircle)
        BadgeStatus.OVERDUE -> Triple(ArkCrimsonBg, ArkCrimsonError, Icons.Filled.Info)
    }

    val label = customText ?: when (status) {
        BadgeStatus.VERIFIED -> "Verified Direct"
        BadgeStatus.PENDING_APPROVAL -> "Pending Admin Review"
        BadgeStatus.ACTIVE -> "Active Listing"
        BadgeStatus.OVERDUE -> "Payment Overdue"
    }

    Row(
        modifier = modifier
            .clip(ArkPillShape)
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Compliance disclosure badge for manual rent recording (Rule 18: ARK never processes money).
 */
@Composable
fun ManualPaymentDisclosureBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ArkShapes.medium)
            .background(ArkCanvas)
            .border(1.dp, ArkBorder, ArkShapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = ArkNavySecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Payment logged manually. Subject to verification by ARK Estate custodian before clearance.",
            style = MaterialTheme.typography.bodySmall,
            color = ArkNavySecondary
        )
    }
}
