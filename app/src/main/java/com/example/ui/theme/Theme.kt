package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = GoldAccent,
    tertiary = SilverGray,
    background = DeepNavy,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = GlowingWhite,
    onSurface = GlowingWhite,
    surfaceVariant = SurfaceCard,
    outline = BorderCyan
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We enforce the premium dark theme by default for the music engine workstation
    dynamicColor: Boolean = false, // We use our polished custom brand colors for consistency rather than android generic dynamic wallpaper tints
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
