package lat.pam.jejakibadah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepTeal = Color(0xFF0F2C30)
val DeepTealSurface = Color(0xFF173B40)
val WarmSand = Color(0xFFFAF7F2)
val WarmCardLight = Color(0xFFFFFFFF)
val SoftGold = Color(0xFFD99B43)
val SoftGoldLight = Color(0xFFF3E2C7)
val TextDark = Color(0xFF1A2426)
val TextMuted = Color(0xFF647477)

private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    primaryContainer = DeepTeal,
    onPrimaryContainer = Color.White,
    secondary = SoftGold,
    onSecondary = DeepTeal,
    secondaryContainer = SoftGoldLight,
    onSecondaryContainer = TextDark,
    background = WarmSand,
    onBackground = TextDark,
    surface = WarmCardLight,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFEDE8DF),
    onSurfaceVariant = TextMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftGold,
    onPrimary = DeepTeal,
    primaryContainer = DeepTealSurface,
    onPrimaryContainer = Color(0xFFF5F2EB),
    secondary = SoftGold,
    onSecondary = DeepTeal,
    secondaryContainer = Color(0xFF234D53),
    onSecondaryContainer = Color(0xFFF5F2EB),
    background = Color(0xFF0A1D20),
    onBackground = Color(0xFFF5F2EB),
    surface = DeepTeal,
    onSurface = Color(0xFFF5F2EB),
    surfaceVariant = DeepTealSurface,
    onSurfaceVariant = Color(0xFFA3B8BB)
)

@Composable
fun JejakIbadahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}