package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ElectricPurpleDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight

/**
 * Custom original vector-drawn IQCut Logo
 * Combines "I" stem, "Q" circle with play triangle inside, and film strip ladder cut accent.
 */
@Composable
fun IQCutIcon(
    size: Dp = 48.dp,
    withGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val gradient = Brush.linearGradient(
                colors = listOf(ElectricPurple, NeonCyan),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )

            val glowBrush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = glowAlpha * 0.4f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.65f
            )

            if (withGlow) {
                drawCircle(brush = glowBrush, radius = w * 0.55f)
            }

            // Letter 'I' (Pill on the left)
            val iLeft = w * 0.14f
            val iTop = h * 0.20f
            val iWidth = w * 0.14f
            val iHeight = h * 0.60f
            val iCorner = iWidth * 0.4f

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(iLeft, iTop),
                size = Size(iWidth, iHeight),
                cornerRadius = CornerRadius(iCorner, iCorner)
            )

            // Letter 'Q' (Ring on the right)
            val qCenter = Offset(w * 0.60f, h * 0.48f)
            val qRadius = w * 0.28f
            val strokeWidth = w * 0.12f

            drawCircle(
                brush = gradient,
                center = qCenter,
                radius = qRadius,
                style = Stroke(width = strokeWidth)
            )

            // Inside the 'Q': Play Symbol (Triangle)
            val playPath = Path().apply {
                val pX = qCenter.x - w * 0.05f
                val pY = qCenter.y - h * 0.08f
                val pSize = w * 0.14f
                moveTo(pX, pY)
                lineTo(pX + pSize * 1.1f, qCenter.y)
                lineTo(pX, pY + pSize * 1.1f)
                close()
            }
            drawPath(
                path = playPath,
                color = Color.White.copy(alpha = 0.9f),
                style = Fill
            )

            // 'Q' Leg Accent: Film Strip / Cut Ladder symbol extending bottom right
            val legStart = Offset(w * 0.68f, h * 0.64f)
            val legEnd = Offset(w * 0.88f, h * 0.84f)
            drawLine(
                brush = Brush.linearGradient(listOf(ElectricPurple, NeonCyanLight)),
                start = legStart,
                end = legEnd,
                strokeWidth = w * 0.09f,
                cap = StrokeCap.Round
            )

            // Film strip cross rungs
            val rung1Start = Offset(w * 0.72f, h * 0.66f)
            val rung1End = Offset(w * 0.78f, h * 0.60f)
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = rung1Start,
                end = rung1End,
                strokeWidth = w * 0.035f,
                cap = StrokeCap.Round
            )

            val rung2Start = Offset(w * 0.80f, h * 0.74f)
            val rung2End = Offset(w * 0.86f, h * 0.68f)
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = rung2Start,
                end = rung2End,
                strokeWidth = w * 0.035f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Full Logo with Icon + "IQCut" Typography
 */
@Composable
fun IQCutFullLogo(
    iconSize: Dp = 38.dp,
    fontSize: Int = 22,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IQCutIcon(size = iconSize)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "IQ",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = fontSize.sp,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Cut",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = fontSize.sp,
                color = NeonCyan,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

/**
 * Logo Card with dark rounded background for watermark or icon display
 */
@Composable
fun IQCutAppBadge(
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = ElectricPurple)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1C1A2E), Color(0xFF0F0F18))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        IQCutIcon(size = size * 0.7f)
    }
}
