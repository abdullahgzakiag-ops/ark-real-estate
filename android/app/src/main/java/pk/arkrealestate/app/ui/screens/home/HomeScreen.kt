package pk.arkrealestate.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.data.model.UserRole
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.BadgeStatus
import pk.arkrealestate.app.ui.components.StatusBadge
import pk.arkrealestate.app.ui.screens.auth.AuthViewModel
import pk.arkrealestate.app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    isUrdu: Boolean,
    onNavigateToMarketplace: () -> Unit,
    onNavigateToSubmitListing: () -> Unit,
    onNavigateToMyListings: () -> Unit,
    onNavigateToSavedListings: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onNavigateToAdminModeration: () -> Unit,
    onNavigateToActivityAlerts: () -> Unit,
    onNavigateToProfileSettings: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionState by viewModel.sessionState.collectAsState()
    val user = sessionState.user
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArkCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ARK Real Estate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = ArkNavyPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notifications icon
                IconButton(onClick = onNavigateToActivityAlerts) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = ArkNavyPrimary
                    )
                }

                // Profile icon
                IconButton(onClick = onNavigateToProfileSettings) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Settings",
                        tint = ArkNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Language toggle pill
                Surface(
                    onClick = { viewModel.toggleLanguage() },
                    color = ArkSurfaceWhite,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = ArkGoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isUrdu) "English" else "اردو",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArkNavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Greeting Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ArkNavyPrimary,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "السلام علیکم" else "Assalam-o-Alaikum",
                        style = MaterialTheme.typography.labelLarge,
                        color = ArkGoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        color = ArkSurfaceWhite.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = user?.role?.name ?: "MEMBER",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArkSurfaceWhite,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user?.fullName ?: "Valued Member",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ArkSurfaceWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = ArkSurfaceWhite.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = user?.phone ?: "+92 300 0000000",
                        style = MaterialTheme.typography.bodySmall,
                        color = ArkSurfaceWhite.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Navigation Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionTile(
                icon = Icons.Default.Search,
                label = if (isUrdu) "مارکیٹ پلیس" else "Marketplace",
                color = ArkNavyPrimary,
                onClick = onNavigateToMarketplace,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.Favorite,
                label = if (isUrdu) "محفوظ شدہ" else "Saved List",
                color = ArkErrorCrimson,
                onClick = onNavigateToSavedListings,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.PostAdd,
                label = if (isUrdu) "نئی لسٹنگ" else "Add Listing",
                color = ArkGoldAccent,
                onClick = onNavigateToSubmitListing,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon = Icons.Default.ListAlt,
                label = if (isUrdu) "میری لسٹنگز" else "My Listings",
                color = ArkSecondarySlate,
                onClick = onNavigateToMyListings,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Marketplace Feed Card
        Surface(
            onClick = onNavigateToMarketplace,
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
                        text = if (isUrdu) "پراپرٹی مارکیٹ پلیس" else "Marketplace Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    StatusBadge(
                        status = BadgeStatus.ACTIVE,
                        customText = "Live"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isUrdu)
                        "لاہور، کراچی، اسلام آباد میں تصدیق شدہ مکانات، فلیٹس اور پلاٹس تلاش کریں، فلٹرز لگائیں اور معائنہ شیڈول کریں۔"
                    else
                        "Browse verified properties across Lahore, Karachi, and Islamabad with price filters (PKR), specifications, and viewing bookings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ArkButton(
                    text = if (isUrdu) "مارکیٹ پلیس کھولیں" else "Browse Marketplace Listings",
                    onClick = onNavigateToMarketplace,
                    variant = ArkButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Property Portfolio & Tenancy Card
        Surface(
            onClick = onNavigateToManagement,
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
                        text = if (isUrdu) "پراپرٹی اور کرایہ داری مینجمنٹ" else "Property Portfolio & Leases",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    StatusBadge(
                        status = BadgeStatus.ACTIVE,
                        customText = "Manager"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isUrdu)
                        "اپنے ملٹی یونٹ پورٹ فولیو کا جائزہ لیں، کرایہ داریاں چیک کریں اور دستی ادائیگیوں کا اندراج کریں (رول 18 مصدقہ لیجر)۔"
                    else
                        "Oversee units, track occupancy rates, record manual rent payments (Rule 18 audited logs), and generate financial yield reports.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ArkButton(
                    text = if (isUrdu) "پورٹ فولیو ڈیش بورڈ کھولیں" else "Open Portfolio Dashboard",
                    onClick = onNavigateToManagement,
                    variant = ArkButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Admin Moderation Hub Card (Rule 17 & 19 Compliance)
        Surface(
            onClick = onNavigateToAdminModeration,
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
                        text = if (isUrdu) "ایڈمن لسٹنگ ماڈریشن پورٹل" else "Admin Moderation Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    StatusBadge(
                        status = BadgeStatus.PENDING_REVIEW,
                        customText = "Vetting Queue"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isUrdu)
                        "رول 17 اور 19: تمام نئی پراپرٹی لسٹنگز کا معائنہ کریں، الاٹمنٹ لیٹر کی تصدیق کریں اور ویریفائیڈ بیج جاری کریں۔"
                    else
                        "Rule 17 & 19 Vetting: Inspect allotment letters, verify CDA/LDA registers, and approve listings with Verified Badges before public publication.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ArkButton(
                    text = if (isUrdu) "ماڈریشن پورٹل کھولیں" else "Open Moderation Portal",
                    onClick = onNavigateToAdminModeration,
                    variant = ArkButtonVariant.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostics Lab Button
        ArkButton(
            text = "Explore Diagnostics Lab",
            onClick = onNavigateToDiagnostics,
            variant = ArkButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Logout Button
        ArkButton(
            text = if (isUrdu) "لاگ آؤٹ" else "Sign Out",
            onClick = {
                viewModel.logout(onLoggedOut = onLoggedOut)
            },
            variant = ArkButtonVariant.ACCENT,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ArkNavyPrimary,
                maxLines = 1
            )
        }
    }
}
