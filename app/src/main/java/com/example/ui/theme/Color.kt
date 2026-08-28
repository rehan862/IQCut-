package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// IQCut Brand Colors
val ElectricPurple = Color(0xFF6C3BFF)
val ElectricPurpleDark = Color(0xFF4A1EC8)
val ElectricPurpleLight = Color(0xFF8E66FF)
val NeonCyan = Color(0xFF00D9FF)
val NeonCyanLight = Color(0xFF5CE5FF)
val NeonCyanDark = Color(0xFF00A8C6)

val DarkBackground = Color(0xFF0B0B10)
val DarkSurface = Color(0xFF15151F)
val DarkSurfaceVariant = Color(0xFF20202F)
val DarkSurfaceContainerHigh = Color(0xFF28283C)
val DarkSurfaceBorder = Color(0xFF2F2F45)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9E9EB5)
val TextMuted = Color(0xFF6A6A82)

val AccentPink = Color(0xFFFF2E93)
val AccentAmber = Color(0xFFFFB800)
val AccentGreen = Color(0xFF00E676)
val AccentRed = Color(0xFFFF3B30)

val BrandGradient = Brush.horizontalGradient(
    colors = listOf(ElectricPurple, NeonCyan)
)

val BrandGradientVertical = Brush.verticalGradient(
    colors = listOf(ElectricPurple, NeonCyan)
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(ElectricPurple.copy(alpha = 0.35f), NeonCyan.copy(alpha = 0.15f), Color.Transparent)
)

val TimelinePlayheadColor = Color(0xFF00D9FF)
