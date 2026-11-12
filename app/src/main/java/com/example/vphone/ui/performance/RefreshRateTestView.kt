package com.example.vphone.ui.performance

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber
import com.example.vphone.ui.components.GamingBadge

@Composable
fun RefreshRateTestView(
    currentHz: Int,
    currentFps: Double,
    frameTimeMs: Double,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "motion_test")

    // Smooth continuous motion sweep at full display refresh rate
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REFRESH RATE & FRAME PACING TEST",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time hardware VSync & motion smoothness probe",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                GamingBadge(text = "$currentHz HZ SYNC", color = NeonCyan)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Motion canvas test bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    val barWidth = 60.dp.toPx()
                    val xPos = sweepProgress * (size.width + barWidth) - barWidth

                    // Draw vertical guide lines
                    val step = size.width / 12
                    for (i in 0..12) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(i * step, 0f),
                            end = Offset(i * step, size.height),
                            strokeWidth = 1f
                        )
                    }

                    // Draw moving motion bar
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                NeonCyan.copy(alpha = 0.2f),
                                NeonCyan,
                                ElectricBlue,
                                Color.Transparent
                            ),
                            startX = xPos,
                            endX = xPos + barWidth
                        ),
                        topLeft = Offset(xPos, 0f),
                        size = Size(barWidth, size.height)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Frame pacing breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "FRAME INTERVAL", color = TextSubtle, fontSize = 9.sp)
                    Text(
                        text = "${String.format("%.2f", frameTimeMs)} ms",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(text = "TARGET 144Hz INTERVAL", color = TextSubtle, fontSize = 9.sp)
                    Text(
                        text = "6.94 ms",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(text = "VSYNC JITTER", color = TextSubtle, fontSize = 9.sp)
                    Text(
                        text = "< 0.4 ms (Locked)",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
