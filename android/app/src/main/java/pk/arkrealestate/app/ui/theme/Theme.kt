package pk.arkrealestate.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColorScheme = lightColorScheme(
    primary = ArkNavyPrimary,
    onPrimary = ArkSurfaceWhite,
    primaryContainer = ArkNavyPrimary,
    onPrimaryContainer = ArkSurfaceContainer,
    secondary = ArkNavySecondary,
    onSecondary = ArkSurfaceWhite,
    secondaryContainer = ArkSurfaceContainerLow,
    onSecondaryContainer = ArkNavySecondary,
    tertiary = ArkGoldAccent,
    onTertiary = ArkNavyPrimary,
    background = ArkCanvas,
    onBackground = ArkTextPrimary,
    surface = ArkSurfaceWhite,
    onSurface = ArkTextPrimary,
    surfaceVariant = ArkSurfaceContainerLow,
    onSurfaceVariant = ArkTextSecondary,
    error = ArkCrimsonError,
    onError = ArkSurfaceWhite
)

@Composable
fun ArkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isRtl: Boolean = false,
    content: @Composable () -> Unit
) {
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = ArkTypography,
            shapes = ArkShapes,
            content = content
        )
    }
}
