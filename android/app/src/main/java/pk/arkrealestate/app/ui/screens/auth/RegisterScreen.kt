package pk.arkrealestate.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import pk.arkrealestate.app.data.model.UserRole
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkPasswordTextField
import pk.arkrealestate.app.ui.components.ArkPhoneTextField
import pk.arkrealestate.app.ui.components.ArkTextField
import pk.arkrealestate.app.ui.components.ValidationErrorBanner
import pk.arkrealestate.app.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.registerState.collectAsState()
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
                onClick = { viewModel.toggleLanguage() },
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

        Spacer(modifier = Modifier.height(24.dp))

        // Title and Subtitle
        Text(
            text = stringResource(id = R.string.create_account),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ArkNavyPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isUrdu)
                "پاکستان کے جدید رئیل اسٹیٹ نیٹ ورک میں شامل ہوں۔"
            else
                "Join Pakistan's verified real estate marketplace & management network.",
            style = MaterialTheme.typography.bodyMedium,
            color = ArkTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Role Selection
        Text(
            text = stringResource(id = R.string.role_select_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ArkNavyPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Role 1: Seeker / Tenant
        RoleOptionCard(
            title = stringResource(id = R.string.role_seeker_title),
            description = stringResource(id = R.string.role_seeker_desc),
            icon = Icons.Default.Person,
            isSelected = uiState.role == UserRole.BUYER_RENTER,
            onClick = { viewModel.onRegisterRoleChanged(UserRole.BUYER_RENTER) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Role 2: Owner / Agent
        RoleOptionCard(
            title = stringResource(id = R.string.role_owner_title),
            description = stringResource(id = R.string.role_owner_desc),
            icon = Icons.Default.RealEstateAgent,
            isSelected = uiState.role == UserRole.OWNER_AGENT,
            onClick = { viewModel.onRegisterRoleChanged(UserRole.OWNER_AGENT) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Role 3: Property Manager
        RoleOptionCard(
            title = stringResource(id = R.string.role_manager_title),
            description = stringResource(id = R.string.role_manager_desc),
            icon = Icons.Default.Apartment,
            isSelected = uiState.role == UserRole.PROPERTY_MANAGER,
            onClick = { viewModel.onRegisterRoleChanged(UserRole.PROPERTY_MANAGER) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message banner if any
        if (uiState.errorMessage != null) {
            ValidationErrorBanner(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.clearRegisterError() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Account Details Inputs
        ArkTextField(
            value = uiState.fullName,
            onValueChange = { viewModel.onRegisterFullNameChanged(it) },
            label = stringResource(id = R.string.full_name_label),
            placeholder = if (isUrdu) "مثلاً محمد احمد" else "e.g. Muhammad Ahmed",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mobile Phone Input (+92 prefix)
        ArkPhoneTextField(
            value = uiState.phone,
            onValueChange = { viewModel.onRegisterPhoneChanged(it) },
            label = stringResource(id = R.string.mobile_label),
            placeholder = stringResource(id = R.string.mobile_placeholder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Input
        ArkPasswordTextField(
            value = uiState.password,
            onValueChange = { viewModel.onRegisterPasswordChanged(it) },
            label = stringResource(id = R.string.password_label),
            modifier = Modifier.fillMaxWidth()
        )

        // Conditional Agent / Manager Fields
        if (uiState.role == UserRole.OWNER_AGENT || uiState.role == UserRole.PROPERTY_MANAGER) {
            Spacer(modifier = Modifier.height(14.dp))
            ArkTextField(
                value = uiState.agencyName,
                onValueChange = { viewModel.onRegisterAgencyNameChanged(it) },
                label = stringResource(id = R.string.agency_name_label),
                placeholder = if (isUrdu) "مثلاً الکریم ایسوسی ایٹس" else "e.g. Al-Karim Associates",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            ArkTextField(
                value = uiState.licenseNumber,
                onValueChange = { viewModel.onRegisterLicenseNumberChanged(it) },
                label = if (isUrdu) "لائسنس یا رجسٹریشن نمبر (اختیاری)" else "License / Reg Number (Optional)",
                placeholder = if (isUrdu) "مثلاً CDA-REG-2024" else "e.g. CDA-REG-2024",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Create Account Button
        ArkButton(
            text = stringResource(id = R.string.create_account),
            onClick = {
                viewModel.register(onSuccess = onRegisterSuccess)
            },
            variant = ArkButtonVariant.PRIMARY,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sign In Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isUrdu) "پہلے سے اکاؤنٹ موجود ہے؟ " else "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = ArkTextSecondary
            )
            Text(
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.bodyMedium,
                color = ArkNavyPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignIn() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Regulatory & Compliance Footnote
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
private fun RoleOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) ArkNavyPrimary.copy(alpha = 0.04f) else ArkSurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ArkNavyPrimary else ArkBorderSubtle
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) ArkNavyPrimary else ArkNavySecondary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) ArkGoldAccent else ArkNavyPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ArkNavyPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArkTextSecondary,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = ArkGoldAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
