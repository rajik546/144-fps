package com.example.vphone.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderGlow
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun GamingCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CardBorder,
    glowEffect: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val borderBrush = if (glowEffect) {
        Brush.linearGradient(listOf(ElectricBlue, NeonCyan, CardBorder))
    } else {
        Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.5f)))
    }

    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, borderBrush)
    ) {
        content()
    }
}

@Composable
fun GamingBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    icon: ImageVector? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun FpsDisplayBadge(
    fps: Int,
    targetFps: Int = 144,
    modifier: Modifier = Modifier
) {
    val color = when {
        fps >= 120 -> NeonCyan
        fps >= 90 -> NeonGreen
        fps >= 55 -> WarningAmber
        else -> CyberPink
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        color.copy(alpha = 0.25f),
                        CardSurfaceVariant
                    )
                )
            )
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$fps",
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "FPS / ${targetFps}Hz",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun StatusIndicatorPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.uppercase()) {
        "RUNNING" -> Pair(NeonGreen, "ACTIVE")
        "STARTING" -> Pair(NeonCyan, "STARTING...")
        "RESTARTING" -> Pair(WarningAmber, "RESTARTING...")
        else -> Pair(TextMuted, "STOPPED")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (status == "RUNNING") color.copy(alpha = alpha) else color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
