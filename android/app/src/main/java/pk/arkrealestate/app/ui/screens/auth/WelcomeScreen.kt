package pk.arkrealestate.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.R
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.theme.*

@Composable
fun WelcomeScreen(
    isUrdu: Boolean,
    onToggleLanguage: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        // Top Navigation Bar: Language switcher and diagnostics button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onNavigateToDiagnostics,
                color = ArkNavySecondary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Diagnostics",
                        tint = ArkNavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Design Lab",
                        style = MaterialTheme.typography.labelMedium,
                        color = ArkNavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                        contentDescription = "Switch Language",
                        tint = ArkGoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "English" else "اردو",
                        style = MaterialTheme.typography.labelMedium,
                        color = ArkNavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Brand Monogram / Crest
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(76.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ArkNavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ARK",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ArkGoldAccent,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline & Subtitle
        Text(
            text = stringResource(id = R.string.welcome_title),
            style = MaterialTheme.typography.headlineSmall,
            color = ArkNavyPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = ArkTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Value Proposition Feature Cards
        FeatureCard(
            icon = Icons.Default.CheckCircle,
            iconTint = ArkEmeraldSuccess,
            title = if (isUrdu) "تصدیق شدہ لسٹنگز" else "Verified Listings",
            description = if (isUrdu) 
                "ہر جائیداد کو منظر عام پر لانے سے قبل تصدیق کیا جاتا ہے۔"
            else 
                "Every property undergoes administrative moderation before public publishing."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            icon = Icons.Default.Apartment,
            iconTint = ArkNavySecondary,
            title = if (isUrdu) "کرایہ داری اور جائیداد کا انتظام" else "Tenancy & Lease Management",
            description = if (isUrdu) 
                "یونٹس، کرایہ نامے اور دستی ادائیگی کے ریکارڈ کا شفاف انتظام۔"
            else 
                "Maintain units, leases, rent schedules, and audited manual payment logs."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            icon = Icons.Default.Handshake,
            iconTint = ArkGoldAccent,
            title = if (isUrdu) "مصدقہ براہ راست رابطہ" else "Verified Direct Inquiries",
            description = if (isUrdu) 
                "مالکان اور مصدقہ ایجنٹس کے ساتھ معائنہ بک کریں اور رابطہ کریں۔"
            else 
                "Connect directly with genuine property owners & licensed estate brokers."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primary & Secondary Action CTAs
        ArkButton(
            text = stringResource(id = R.string.create_account),
            onClick = onNavigateToRegister,
            variant = ArkButtonVariant.PRIMARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ArkButton(
            text = stringResource(id = R.string.sign_in),
            onClick = onNavigateToSignIn,
            variant = ArkButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Trust & Regulatory Footnote
        Text(
            text = stringResource(id = R.string.cda_lda_compliant),
            style = MaterialTheme.typography.bodySmall,
            color = ArkTextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, ArkBorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkNavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
