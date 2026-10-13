package pk.arkrealestate.app.ui.screens.notifications

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
import pk.arkrealestate.app.ui.theme.*

data class ActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val timeAgo: String,
    val type: String, // "viewing", "rent", "approval", "inquiry"
    val isUnread: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityAlertsScreen(
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var markAllReadTrigger by remember { mutableStateOf(false) }

    val notifications = remember(markAllReadTrigger) {
        listOf(
            ActivityItem(
                id = "1",
                title = "Viewing Request Confirmed",
                subtitle = "1 Kanal Modern Villa • DHA Phase 6, Sector J",
                description = "Brig. Tariq confirmed scheduled inspection for tomorrow at 4:30 PM PKT. Security pre-cleared.",
                timeAgo = "12m ago",
                type = "viewing",
                isUnread = !markAllReadTrigger
            ),
            ActivityItem(
                id = "2",
                title = "Listing Verified & Approved",
                subtitle = "10 Marla Luxury Villa • DHA Phase 6",
                description = "ARK Custodian verified allotment letter and granted Verified Badge. Your listing is now live on marketplace search.",
                timeAgo = "1h ago",
                type = "approval",
                isUnread = !markAllReadTrigger
            ),
            ActivityItem(
                id = "3",
                title = "Manual Rent Payment Logged",
                subtitle = "Villa 42-B, Sector J • Tenancy Ledger",
                description = "Manual receipt for PKR 185,000 (Meezan Bank IBFT #98421) recorded into tenancy audit ledger.",
                timeAgo = "3h ago",
                type = "rent",
                isUnread = false
            ),
            ActivityItem(
                id = "4",
                title = "New Buyer Inquiry Received",
                subtitle = "Sector J Designer Plot • Lahore",
                description = "Ali Hassan submitted an inquiry: 'Is this corner plot with possession ready for immediate transfer?'",
                timeAgo = "5h ago",
                type = "inquiry",
                isUnread = false
            )
        )
    }

    val filteredList = remember(notifications, selectedCategory) {
        when (selectedCategory) {
            "viewing" -> notifications.filter { it.type == "viewing" }
            "rent" -> notifications.filter { it.type == "rent" }
            "approval" -> notifications.filter { it.type == "approval" }
            else -> notifications
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isUrdu) "اطلاعات اور سرگرمیاں" else "Activity Center",
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
                    TextButton(onClick = { markAllReadTrigger = true }) {
                        Text(
                            text = if (isUrdu) "سب پڑھ لیں" else "Mark all read",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArkGoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArkSurfaceWhite)
            )
        },
        containerColor = ArkCanvas,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Pills
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterPill(
                            title = if (isUrdu) "سب" else "All (4)",
                            isSelected = selectedCategory == "all",
                            onClick = { selectedCategory = "all" }
                        )
                    }
                    item {
                        FilterPill(
                            title = if (isUrdu) "معائنہ" else "Viewings (1)",
                            isSelected = selectedCategory == "viewing",
                            onClick = { selectedCategory = "viewing" }
                        )
                    }
                    item {
                        FilterPill(
                            title = if (isUrdu) "منظوری" else "Approvals (1)",
                            isSelected = selectedCategory == "approval",
                            onClick = { selectedCategory = "approval" }
                        )
                    }
                    item {
                        FilterPill(
                            title = if (isUrdu) "کرایہ لیجر" else "Rent Ledger (1)",
                            isSelected = selectedCategory == "rent",
                            onClick = { selectedCategory = "rent" }
                        )
                    }
                }
            }

            items(filteredList, key = { it.id }) { item ->
                NotificationCard(item = item)
            }
        }
    }
}

@Composable
private fun FilterPill(
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
private fun NotificationCard(item: ActivityItem) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (item.isUnread) ArkGoldAccent.copy(alpha = 0.5f) else ArkBorderSubtle),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (item.type) {
                        "viewing" -> Icons.Default.CalendarMonth
                        "approval" -> Icons.Default.VerifiedUser
                        "rent" -> Icons.Default.Payments
                        else -> Icons.Default.Chat
                    }
                    val iconColor = when (item.type) {
                        "viewing" -> ArkNavyPrimary
                        "approval" -> ArkSuccessEmerald
                        "rent" -> ArkWarningAmber
                        else -> ArkGoldAccent
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = iconColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(text = item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
                        Text(text = item.subtitle, style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
                    }
                }

                Text(text = item.timeAgo, style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = ArkNavyPrimary,
                lineHeight = 18.sp
            )
        }
    }
}
