package pk.arkrealestate.app.ui.screens.profile

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
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.screens.auth.AuthViewModel
import pk.arkrealestate.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    viewModel: AuthViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
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
    ) {
        // App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isUrdu) "پروفائل اور ترتیبات" else "Profile & Account Settings",
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
            // Profile Info Hero Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ArkNavyPrimary,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ArkGoldAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.fullName ?: "Tariq Mehmood",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ArkNavyPrimary
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ArkGoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = user?.role?.name ?: "OWNER_AGENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ArkNavyPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact info rows
                    InfoRow(icon = Icons.Default.Phone, label = if (isUrdu) "موبائل نمبر" else "Phone Number", value = user?.phone ?: "+92 300 8459201")
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(icon = Icons.Default.Email, label = if (isUrdu) "ای میل" else "Email Address", value = user?.email ?: "tariq.mehmood@example.com")
                    if (!user?.agencyName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        InfoRow(icon = Icons.Default.Business, label = if (isUrdu) "ایجنسی" else "Agency", value = user!!.agencyName!!)
                    }
                }
            }

            // NADRA Identity Verification Ribbon
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ArkSuccessEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = ArkSuccessEmerald, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(text = "NADRA Citizen Verification", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
                            Text(text = "CNIC: 35201-********-1", style = MaterialTheme.typography.labelSmall, color = ArkTextSecondary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ArkSuccessEmerald.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Verified",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ArkSuccessEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Language & Regional Settings
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "زبان اور علاقائی ترتیبات" else "Language & Localization",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArkNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArkCanvas)
                            .padding(4.dp)
                    ) {
                        Surface(
                            onClick = { if (isUrdu) viewModel.toggleLanguage() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isUrdu) ArkNavyPrimary else Color.Transparent
                        ) {
                            Text(
                                text = "English (LTR)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (!isUrdu) ArkSurfaceWhite else ArkNavyPrimary,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            onClick = { if (!isUrdu) viewModel.toggleLanguage() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isUrdu) ArkNavyPrimary else Color.Transparent
                        ) {
                            Text(
                                text = "اردو (RTL)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isUrdu) ArkSurfaceWhite else ArkNavyPrimary,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // App Version & Build
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ArkSurfaceWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "App Version", style = MaterialTheme.typography.bodyMedium, color = ArkNavyPrimary)
                    Text(text = "1.0.0 (Production Release)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = ArkTextSecondary)
                }
            }

            // Sign Out Button
            ArkButton(
                text = if (isUrdu) "اکاؤنٹ سے لاگ آؤٹ کریں" else "Sign Out of ARK Real Estate",
                onClick = { viewModel.logout(onLoggedOut = onLoggedOut) },
                variant = ArkButtonVariant.ACCENT,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArkCanvas)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ArkTextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = ArkTextSecondary)
        }

        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ArkNavyPrimary)
    }
}
