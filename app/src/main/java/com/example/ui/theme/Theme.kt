package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FantasyColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBg,
    primaryContainer = GoldSecondary,
    onPrimaryContainer = Color.White,
    secondary = GoldLight,
    onSecondary = ObsidianBg,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = GoldLight,
    tertiary = MageBlue,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FantasyColorScheme,
        typography = Typography,
        content = content
    )
}
