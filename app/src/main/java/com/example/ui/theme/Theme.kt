package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IQCutColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = Color.White,
    primaryContainer = ElectricPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = Color(0xFF051016),
    secondaryContainer = Color(0xFF003845),
    onSecondaryContainer = NeonCyanLight,
    tertiary = AccentPink,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceVariant,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun IQCutTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IQCutColorScheme,
        typography = Typography,
        content = content
    )
}
