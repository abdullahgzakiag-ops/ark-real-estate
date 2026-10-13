package pk.arkrealestate.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pk.arkrealestate.app.ui.theme.*

enum class ArkButtonVariant {
    PRIMARY,
    SECONDARY,
    ACCENT
}

@Composable
fun ArkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ArkButtonVariant = ArkButtonVariant.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val buttonColors = when (variant) {
        ArkButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = ArkNavyPrimary,
            contentColor = ArkSurfaceWhite,
            disabledContainerColor = ArkBorderLight,
            disabledContentColor = ArkTextMuted
        )
        ArkButtonVariant.SECONDARY -> ButtonDefaults.outlinedButtonColors(
            containerColor = ArkSurfaceWhite,
            contentColor = ArkNavySecondary
        )
        ArkButtonVariant.ACCENT -> ButtonDefaults.buttonColors(
            containerColor = ArkGoldAccent,
            contentColor = ArkNavyPrimary
        )
    }

    val border = if (variant == ArkButtonVariant.SECONDARY) {
        BorderStroke(1.5.dp, ArkNavySecondary)
    } else null

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isLoading,
        shape = ArkShapes.medium, // 12dp border radius
        colors = buttonColors,
        border = border,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = if (variant == ArkButtonVariant.ACCENT) ArkNavyPrimary else ArkSurfaceWhite,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
