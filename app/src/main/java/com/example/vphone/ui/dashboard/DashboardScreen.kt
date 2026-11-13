package com.example.vphone.ui.dashboard

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.vphone.data.model.VirtualGame
import com.example.vphone.data.model.VirtualPhone
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.FpsDisplayBadge
import com.example.vphone.ui.components.GamingBadge
import com.example.vphone.ui.components.GamingCard
import com.example.vphone.ui.components.StatusIndicatorPill
import com.example.vphone.ui.performance.FeasibilityReportDialog
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToPhones: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val displayCaps by viewModel.displayCaps.collectAsStateWithLifecycle()
    val fpsReading by viewModel.fpsReading.collectAsStateWithLifecycle()
    val runningPhone by viewModel.runningPhone.collectAsStateWithLifecycle()
    val allPhones by viewModel.allPhones.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val allGames by viewModel.allGames.collectAsStateWithLifecycle()
    val installedPackages by viewModel.installedPackages.collectAsStateWithLifecycle()
    val systemStats by viewModel.systemStats.collectAsStateWithLifecycle()
    val feasibility by viewModel.feasibility.collectAsStateWithLifecycle()

    var showFeasibilityModal by remember { mutableStateOf(false) }

    val activePhone = runningPhone ?: allPhones.firstOrNull()
    val activeProfile = allProfiles.firstOrNull { it.id == activePhone?.deviceProfileId }

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
            // Dashboard Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "V-PHONE",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        GamingBadge(text = "144 FPS ENGINE", color = NeonCyan)
                    }
                    Text(
                        text = "Android Virtual Device & High-Refresh Gaming Space",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleFloatingHud() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CardSurfaceVariant)
                            .testTag("dashboard_hud_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Toggle Floating HUD",
                            tint = NeonCyan
                        )
                    }
                }
            }
        }

        // Live Real-Time FPS and Display Metrics
        item {
            GamingCard(
                glowEffect = true,
                modifier = Modifier.fillMaxWidth().testTag("live_fps_hero_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REAL-TIME FRAME METER",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${fpsReading.currentFps.roundToInt()}",
                                    color = NeonCyan,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FPS",
                                    color = ElectricBlue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            GamingBadge(
                                text = "PHYSICAL: ${displayCaps.currentRefreshRateInt} Hz",
                                color = if (displayCaps.currentRefreshRateInt >= 120) NeonGreen else WarningAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Frame Time: ${String.format("%.2f", fpsReading.frameTimeMs)} ms",
                                color = TextWhite,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "1% Low: ${fpsReading.onePercentLowFps.roundToInt()} FPS",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Physical vs Virtual Spec pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCharcoal)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HOST: ${feasibility.physicalManufacturer} ${feasibility.physicalModel}",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = feasibility.physicalOsVersion,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.lockMaxRefreshRate(activity) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("quick_lock_144hz_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LOCK 144Hz",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Active Virtual Phone Hero
        item {
            GamingCard(
                borderColor = CardBorderGlow,
                modifier = Modifier.fillMaxWidth().testTag("active_virtual_phone_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activePhone?.name ?: "V-Phone Instance #1",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusIndicatorPill(status = activePhone?.status ?: "STOPPED")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Spec Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "SPOOFED IDENTITY", color = TextSubtle, fontSize = 10.sp)
                            Text(
                                text = activeProfile?.model ?: "Infinix X6891",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Brand: ${activeProfile?.brand ?: "INFINIX"}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Column {
                            Text(text = "TARGET FPS & RES", color = TextSubtle, fontSize = 10.sp)
                            Text(
                                text = "${activePhone?.targetRefreshRate ?: 144} FPS Target",
                                color = NeonGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activePhone?.targetResolution ?: "1080x2400",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Column {
                            Text(text = "SANDBOX RAM", color = TextSubtle, fontSize = 10.sp)
                            Text(
                                text = "${activePhone?.allocatedRamMb ?: 4096} MB",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activePhone?.allocatedStorageGb ?: 32} GB Storage",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                activePhone?.let { viewModel.launchVirtualDesktop(it) }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("launch_virtual_desktop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = VoidBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OPEN V-DESKTOP",
                                color = VoidBlack,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        if (activePhone?.status == "RUNNING") {
                            OutlinedButton(
                                onClick = { activePhone.let { viewModel.stopPhone(it) } },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPink),
                                modifier = Modifier.testTag("stop_virtual_phone_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = CyberPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "STOP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { activePhone?.let { viewModel.startPhone(it) } },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                modifier = Modifier.testTag("start_virtual_phone_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = VoidBlack,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "START",
                                    color = VoidBlack,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hardware Virtualization Feasibility Banner
        item {
            GamingCard(
                borderColor = if (feasibility.avfAvailable) NeonGreen else CardBorder,
                onClick = { showFeasibilityModal = true },
                modifier = Modifier.fillMaxWidth().testTag("feasibility_summary_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (feasibility.avfAvailable) Icons.Default.CheckCircle else Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (feasibility.avfAvailable) NeonGreen else NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ENGINE ARCHITECTURE",
                                color = TextSubtle,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = feasibility.recommendedEngineMode,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap for Android 15 Hypervisor & Anti-Cheat Analysis",
                                color = NeonCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                    GamingBadge(text = "REPORT", color = NeonCyan)
                }
            }
        }

        // Quick Virtual Game Library
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARGET GAMES (144 FPS PRESETS)",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "VIEW ALL (${allGames.size})",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToGames() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allGames) { game ->
                        val isInstalled = installedPackages.contains(game.packageName)
                        GameQuickCard(
                            game = game,
                            isInstalled = isInstalled,
                            onClick = {
                                if (isInstalled) {
                                    viewModel.launchRealGame(context, game)
                                } else {
                                    viewModel.selectGameForDetail(game)
                                    onNavigateToGames()
                                }
                            }
                        )
                    }
                }
            }
        }

        // System Telemetry Row
        item {
            GamingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DEVICE TELEMETRY & RESOURCES",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "RAM USAGE", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${systemStats.ramUsagePercent}%",
                                color = if (systemStats.ramUsagePercent > 85) WarningAmber else NeonGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${systemStats.usedRamMb}/${systemStats.totalRamMb} MB",
                                color = TextSubtle,
                                fontSize = 10.sp
                            )
                        }

                        Column {
                            Text(text = "BATTERY & TEMP", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${systemStats.batteryPercent}% • ${systemStats.batteryTemperatureC}°C",
                                color = if (systemStats.batteryTemperatureC > 42f) CyberPink else TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (systemStats.isCharging) "Fast Charging" else "Discharging",
                                color = TextSubtle,
                                fontSize = 10.sp
                            )
                        }

                        Column {
                            Text(text = "CPU CORES", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${systemStats.cpuCores} Cores Active",
                                color = NeonCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Host Guard: OK",
                                color = TextSubtle,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GameQuickCard(
    game: VirtualGame,
    isInstalled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(175.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isInstalled) NeonGreen.copy(alpha = 0.5f) else CardBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                if (isInstalled) listOf(NeonGreen, ElectricBlue)
                                else listOf(ElectricBlue, NeonCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.appName.take(2).uppercase(),
                        color = VoidBlack,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                GamingBadge(
                    text = if (isInstalled) "INSTALLED" else "${game.preferredFps} FPS",
                    color = if (isInstalled) NeonGreen else NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = game.appName,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = if (isInstalled) "Tap to Launch Real Game" else "Tap to Install / Config",
                color = if (isInstalled) NeonGreen else TextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = if (isInstalled) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
