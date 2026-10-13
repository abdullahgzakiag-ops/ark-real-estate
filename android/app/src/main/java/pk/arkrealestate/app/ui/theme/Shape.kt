package pk.arkrealestate.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ArkShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),   // Buttons & Inputs (0.75rem / 12dp)
    large = RoundedCornerShape(16.dp),    // Cards & Sheets (1rem / 16dp)
    extraLarge = RoundedCornerShape(24.dp) // Bottom Sheets (1.5rem / 24dp)
)

val ArkPillShape = RoundedCornerShape(9999.dp)
