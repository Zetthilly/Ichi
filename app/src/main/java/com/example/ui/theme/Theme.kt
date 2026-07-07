package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,         // Electric Blue (#00B7FF)
    secondary = RoyalGold,          // Royal Gold (#FFD54A)
    tertiary = CosmicPurple,        // Cosmic Purple (#6D4CFF)
    background = DeepNavy,          // Deep Navy (#030A16)
    surface = MidnightSlate,        // Midnight Slate (#0D1726)
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = TextSilver,      // Text Silver (#C9D1D9)
    onSurface = TextSilver,         // Text Silver (#C9D1D9)
    surfaceVariant = SurfaceCard,
    outline = NeonCyan              // Neon Cyan (#00F0FF)
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
