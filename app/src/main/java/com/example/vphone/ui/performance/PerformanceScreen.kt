package com.example.vphone.ui.performance

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderGlow
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.WarningAmber
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.GamingBadge
import com.example.vphone.ui.components.GamingCard
import kotlin.math.roundToInt

@Composable
fun PerformanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val displayCaps by viewModel.displayCaps.collectAsStateWithLifecycle()
    val fpsReading by viewModel.fpsReading.collectAsStateWithLifecycle()
    val gpuReport by viewModel.gpuReport.collectAsStateWithLifecycle()
    val feasibility by viewModel.feasibility.collectAsStateWithLifecycle()
    val showFloatingHud by viewModel.showFloatingHud.collectAsStateWithLifecycle()

    var showFeasibilityModal by remember { mutableStateOf(false) }
    var selectedTargetFps by remember { mutableIntStateOf(144) }
    var selectedGraphicsProfile by remember { mutableStateOf("High Performance") }

    if (showFeasibilityModal) {
        FeasibilityReportDialog(
            report = feasibility,
            onDismiss = { showFeasibilityModal = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PERFORMANCE & DISPLAY",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "144 FPS Engine, Display Refresh Modes & GPU Diagnostics",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshHardwareState(activity) },
                    modifier = Modifier.clip(CircleShape).background(CardSurfaceVariant).testTag("refresh_hardware_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Hardware", tint = NeonCyan)
                }
            }
        }

        // 144 FPS Gaming Configuration Card
        item {
            GamingCard(glowEffect = true, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FPS TARGET PREFERENCE",
                            color = TextSubtle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        GamingBadge(
                            text = if (displayCaps.supports144Hz) "144Hz DISPLAY READY" else "UP TO ${displayCaps.maxSupportedRefreshRate}Hz SUPPORTED",
                            color = if (displayCaps.supports144Hz) NeonGreen else WarningAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FPS buttons: 30, 40, 45, 60, 90, 120, 144
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(30, 40, 45, 60, 90, 120, 144).forEach { fps ->
                            val selected = (selectedTargetFps == fps)
                            val isPhysicallySupported = fps <= displayCaps.maxSupportedRefreshRate

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (selected) NeonCyan else if (isPhysicallySupported) CardBorder else CardBorder.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedTargetFps = fps
                                        viewModel.applyRefreshRate(activity, fps)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$fps",
                                        color = if (selected) Color.White else if (isPhysicallySupported) TextWhite else TextMuted,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "FPS",
                                        color = if (selected) NeonCyan else TextSubtle,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Active Target: $selectedTargetFps FPS. Android Window timing locked to display VSync.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Motion Test View
        item {
            RefreshRateTestView(
                currentHz = displayCaps.currentRefreshRateInt,
                currentFps = fpsReading.currentFps,
                frameTimeMs = fpsReading.frameTimeMs
            )
        }

        // Display Modes Manager
        item {
            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DISPLAY REFRESH RATE MODES",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { viewModel.lockMaxRefreshRate(activity) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("apply_max_hz_button")
                        ) {
                            Text(
                                text = "LOCK MAX HZ",
                                color = VoidBlack,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    displayCaps.supportedModes.take(5).forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (mode.isCurrent) DarkCharcoal else Color.Transparent)
                                .border(1.dp, if (mode.isCurrent) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.applyRefreshRate(activity, mode.refreshRateInt) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${mode.width} x ${mode.height}",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (mode.isCurrent) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    GamingBadge(text = "ACTIVE", color = NeonGreen)
                                }
                            }
                            Text(
                                text = "${String.format("%.1f", mode.refreshRate)} Hz",
                                color = if (mode.refreshRateInt >= 144) NeonGreen else if (mode.refreshRateInt >= 120) NeonCyan else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // GPU & Graphics Configuration
        item {
            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GPU & GRAPHICS DIAGNOSTICS",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DarkCharcoal
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "RENDERER: ${gpuReport.glRenderer}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "VENDOR: ${gpuReport.glVendor} • ${gpuReport.glVersion}", color = TextWhite, fontSize = 11.sp)
                            Text(text = "VULKAN: ${gpuReport.vulkanVersion} (Hardware Level ${gpuReport.vulkanHardwareLevel})", color = NeonGreen, fontSize = 11.sp)
                            Text(text = "EXTENSIONS: ${gpuReport.extensionsCount} extensions • Max Texture: ${gpuReport.maxTextureSize}px", color = TextMuted, fontSize = 10.sp)
                            Text(text = "ACCELERATION: ${gpuReport.guestAccelerationStatus}", color = TextSubtle, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Graphics Preferences Toggle
                    Text(text = "GRAPHICS PREFERENCE PROFILE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Standard", "Balanced", "High Performance", "Compatibility").forEach { mode ->
                            val selected = (selectedGraphicsProfile == mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                    .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedGraphicsProfile = mode }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.take(8),
                                    color = if (selected) Color.White else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Gaming Performance Monitor (HUD)
        item {
            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FLOATING GAMING MONITOR (HUD)",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-time FPS, Hz & system telemetry overlay",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = showFloatingHud,
                            onCheckedChange = { viewModel.toggleFloatingHud() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = ElectricBlue,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkCharcoal
                            ),
                            modifier = Modifier.testTag("in_app_hud_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startSystemOverlayService(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("start_system_overlay_button")
                        ) {
                            Icon(imageVector = Icons.Default.Window, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "START SYSTEM OVERLAY", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.stopSystemOverlayService(context) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPink)
                        ) {
                            Text(text = "STOP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Technical Concepts Explanation Card
        item {
            GamingCard(
                borderColor = CardBorder,
                onClick = { showFeasibilityModal = true },
                modifier = Modifier.fillMaxWidth().testTag("view_feasibility_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UNDERSTANDING 144 FPS ARCHITECTURE",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Display Refresh Rate (Hz): How many times the physical screen refreshes each second (e.g., 144Hz = once every 6.94ms).\n• Game Rendering FPS: How many distinct frame images the GPU draws each second.\n• Frame Pacing & VSync: Synchronizing game engine draw calls with hardware scanout to eliminate micro-stutter and frame judder.",
                        color = TextWhite,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to open Full Hardware Feasibility & Hypervisor Diagnostic Report →",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
