package com.example.vphone.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber
import kotlin.math.roundToInt

@Composable
fun FloatingHudOverlay(
    fps: Double,
    displayHz: Int,
    ramPercent: Int,
    batteryTempC: Float,
    activeProfileName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(40f) }
    var offsetY by remember { mutableFloatStateOf(160f) }
    var isExpanded by remember { mutableStateOf(false) }
    var opacity by remember { mutableFloatStateOf(0.92f) }

    val fpsInt = fps.roundToInt()
    val fpsColor = when {
        fpsInt >= 120 -> NeonCyan
        fpsInt >= 90 -> NeonGreen
        fpsInt >= 55 -> WarningAmber
        else -> CyberPink
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface.copy(alpha = opacity))
            .border(
                1.5.dp,
                Brush.linearGradient(listOf(ElectricBlue, NeonCyan, Color.Transparent)),
                RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
            .testTag("floating_hud_overlay")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(if (isExpanded) 220.dp else 160.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(fpsColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$fpsInt FPS",
                        color = fpsColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(24.dp).testTag("hud_toggle_expand")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand HUD",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp).testTag("hud_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close HUD",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = "$displayHz Hz • RAM: $ramPercent% • $batteryTempC°C",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "V-Profile: $activeProfileName",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mode: 144 FPS Hardware VSync",
                        color = TextWhite,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Frame Pacing: 6.9ms (Target)",
                        color = NeonGreen,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
