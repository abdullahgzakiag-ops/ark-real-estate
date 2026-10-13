package pk.arkrealestate.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.arkrealestate.app.R
import pk.arkrealestate.app.ui.components.ArkButton
import pk.arkrealestate.app.ui.components.ArkButtonVariant
import pk.arkrealestate.app.ui.components.ArkPasswordTextField
import pk.arkrealestate.app.ui.components.ArkPhoneTextField
import pk.arkrealestate.app.ui.components.ValidationErrorBanner
import pk.arkrealestate.app.ui.theme.*

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    isUrdu: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.loginState.collectAsState()
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

        Spacer(modifier = Modifier.height(28.dp))

        // Title and Subtitle
        Text(
            text = stringResource(id = R.string.welcome_back),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ArkNavyPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.sign_in_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = ArkTextSecondary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Error message banner if any
        if (uiState.errorMessage != null) {
            ValidationErrorBanner(
                message = uiState.errorMessage!!,
                onDismiss = { viewModel.clearLoginError() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mobile Phone Input (+92 prefix)
        ArkPhoneTextField(
            value = uiState.phone,
            onValueChange = { viewModel.onLoginPhoneChanged(it) },
            label = stringResource(id = R.string.mobile_label),
            placeholder = stringResource(id = R.string.mobile_placeholder),
            isError = uiState.errorMessage != null && uiState.phone.length < 10,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        ArkPasswordTextField(
            value = uiState.password,
            onValueChange = { viewModel.onLoginPasswordChanged(it) },
            label = stringResource(id = R.string.password_label),
            isError = uiState.errorMessage != null && uiState.password.length < 8,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sign In Button
        ArkButton(
            text = stringResource(id = R.string.btn_sign_in),
            onClick = {
                viewModel.login(onSuccess = onSignInSuccess)
            },
            variant = ArkButtonVariant.PRIMARY,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Register link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isUrdu) "اکاؤنٹ نہیں ہے؟ " else "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = ArkTextSecondary
            )
            Text(
                text = stringResource(id = R.string.create_account),
                style = MaterialTheme.typography.bodyMedium,
                color = ArkNavyPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(36.dp))

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
