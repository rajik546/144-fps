package com.example.vphone.ui.virtualos

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.data.model.VirtualGame
import com.example.vphone.data.model.VirtualPhone
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.GamingBadge
import kotlin.math.roundToInt

data class VirtualAppItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val isGame: Boolean = false,
    val gamePackage: String? = null
)

@Composable
fun VirtualPhoneDesktopScreen(
    phone: VirtualPhone,
    profile: DeviceProfile?,
    viewModel: MainViewModel,
    onExitDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        onExitDesktop()
    }

    val fpsReading by viewModel.fpsReading.collectAsStateWithLifecycle()
    val allGames by viewModel.allGames.collectAsStateWithLifecycle()
    val installedPackages by viewModel.installedPackages.collectAsStateWithLifecycle()
    val engineLogs by viewModel.virtualEngine.logs.collectAsStateWithLifecycle()

    var activeDialogType by remember { mutableStateOf<String?>(null) }
    var selectedUninstalledGame by remember { mutableStateOf<VirtualGame?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshInstalledPackages(context)
    }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.installApk(context, uri)
        }
    }

    // Dialogs
    if (activeDialogType == "SETTINGS") {
        VirtualSettingsModal(
            phone = phone,
            profile = profile,
            onDismiss = { activeDialogType = null }
        )
    }

    if (activeDialogType == "TERMINAL") {
        VirtualTerminalModal(
            logs = engineLogs,
            onDismiss = { activeDialogType = null }
        )
    }

    if (activeDialogType == "GAME_NOT_INSTALLED" && selectedUninstalledGame != null) {
        GameNotInstalledDialog(
            game = selectedUninstalledGame!!,
            profile = profile,
            onDismiss = {
                activeDialogType = null
                selectedUninstalledGame = null
            },
            onInstallFromPlayStore = {
                viewModel.openPlayStore(context, selectedUninstalledGame!!.packageName)
            },
            onPickApk = {
                apkPickerLauncher.launch("application/vnd.android.package-archive")
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D14),
                        Color(0xFF0B1320),
                        Color(0xFF05070B)
                    )
                )
            )
            .testTag("virtual_desktop_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Virtual Status Bar
            VirtualStatusBar(
                phone = phone,
                profile = profile,
                onExit = onExitDesktop
            )

            // Virtual Home Screen Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Virtual Phone Widget Clock
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "12:00",
                        color = TextWhite,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "144 FPS V-RIG RUNNING",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                    }
                    Text(
                        text = "${profile?.manufacturer} ${profile?.model} • ${phone.targetRefreshRate}Hz Active Identity",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Gaming Engine Quick Controls
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardSurface.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "144 FPS REAL GAME LAUNCHER",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap any installed game to launch directly with HUD",
                                    color = NeonGreen,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        GamingBadge(text = "${phone.allocatedRamMb} MB ALLOCATED", color = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App Launcher Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIRTUAL APPLICATIONS & REAL GAMES",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "TAP TO LAUNCH",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // App Grid: 4 columns
                val systemApps = listOf(
                    VirtualAppItem("settings", "Settings", Icons.Default.Settings, ElectricBlue),
                    VirtualAppItem("terminal", "Logcat", Icons.Default.Terminal, Color(0xFF333344)),
                    VirtualAppItem("files", "Sandbox", Icons.Default.Folder, WarningAmber),
                    VirtualAppItem("turbo", "Game Turbo", Icons.Default.Bolt, CyberPink)
                )

                val gameApps = allGames.map { g ->
                    VirtualAppItem(
                        id = g.packageName,
                        name = g.appName,
                        icon = Icons.Default.Games,
                        iconBgColor = NeonCyan,
                        isGame = true,
                        gamePackage = g.packageName
                    )
                }

                val allDesktopApps = systemApps + gameApps

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(330.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allDesktopApps) { app ->
                        val isInstalled = app.isGame && installedPackages.contains(app.id)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    when (app.id) {
                                        "settings" -> activeDialogType = "SETTINGS"
                                        "terminal" -> activeDialogType = "TERMINAL"
                                        "files" -> viewModel.showToast("Isolated Virtual Storage: /data/vphone_${phone.id}/ (Active)")
                                        "turbo" -> viewModel.showToast("Game Turbo 144Hz Active with ${profile?.model ?: "ROG Phone 8"}")
                                        else -> {
                                            val targetGame = allGames.firstOrNull { it.packageName == app.id }
                                            if (targetGame != null) {
                                                if (installedPackages.contains(targetGame.packageName)) {
                                                    // Launch real installed game immediately!
                                                    viewModel.launchRealGame(context, targetGame, phone.targetRefreshRate)
                                                } else {
                                                    selectedUninstalledGame = targetGame
                                                    activeDialogType = "GAME_NOT_INSTALLED"
                                                }
                                            }
                                        }
                                    }
                                }
                                .padding(vertical = 4.dp)
                                .testTag("desktop_app_${app.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (app.isGame) {
                                            if (isInstalled) Brush.linearGradient(listOf(NeonGreen, ElectricBlue))
                                            else Brush.linearGradient(listOf(ElectricBlue, NeonCyan))
                                        } else {
                                            Brush.linearGradient(listOf(app.iconBgColor, CardSurfaceVariant))
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isInstalled) NeonGreen else CardBorder,
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (app.isGame) {
                                    Text(
                                        text = app.name.take(2).uppercase(),
                                        color = VoidBlack,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                } else {
                                    Icon(
                                        imageVector = app.icon,
                                        contentDescription = app.name,
                                        tint = if (app.isGame) VoidBlack else TextWhite,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = app.name,
                                color = if (isInstalled) NeonGreen else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isInstalled) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Exit Bar
                Button(
                    onClick = onExitDesktop,
                    colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("exit_virtual_desktop_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "EXIT V-PHONE DESKTOP", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun VirtualStatusBar(
    phone: VirtualPhone,
    profile: DeviceProfile?,
    onExit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkCharcoal.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "5G V-NET",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                GamingBadge(
                    text = "${profile?.model ?: "Infinix X6891"} | ${phone.targetRefreshRate}Hz",
                    color = NeonGreen
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.BatteryFull, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = onExit, modifier = Modifier.size(26.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun VirtualSettingsModal(
    phone: VirtualPhone,
    profile: DeviceProfile?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            color = CardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIRTUAL DEVICE SETTINGS",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkCharcoal
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "ACTIVE BUILD PROPERTIES", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Build.MANUFACTURER: ${profile?.manufacturer ?: "INFINIX"}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Build.BRAND: ${profile?.brand ?: "INFINIX"}", color = TextWhite, fontSize = 12.sp)
                        Text(text = "Build.MODEL: ${profile?.model ?: "Infinix X6891"}", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Build.DEVICE: ${profile?.deviceName ?: "X6891-NEO"}", color = TextWhite, fontSize = 12.sp)
                        Text(text = "Build.VERSION.RELEASE: ${profile?.androidVersion ?: "Android 15"}", color = TextWhite, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkCharcoal
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "HARDWARE RESOURCES", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Allocated RAM: ${phone.allocatedRamMb} MB", color = TextWhite, fontSize = 12.sp)
                        Text(text = "Storage Quota: ${phone.allocatedStorageGb} GB", color = TextWhite, fontSize = 12.sp)
                        Text(text = "Display Resolution: ${phone.targetResolution}", color = TextWhite, fontSize = 12.sp)
                        Text(text = "Target Refresh Rate: ${phone.targetRefreshRate} Hz", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Graphics Mode: ${phone.graphicsMode}", color = ElectricBlue, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "CLOSE SETTINGS", color = VoidBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun VirtualTerminalModal(
    logs: List<com.example.vphone.engine.VirtualEngineEvent>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            color = Color(0xFF07090E)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIRTUAL ENGINE LOGCAT",
                        color = NeonCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    logs.forEach { log ->
                        val color = when (log.level) {
                            "SUCCESS" -> NeonGreen
                            "WARN" -> WarningAmber
                            "ERROR" -> CyberPink
                            else -> TextWhite
                        }
                        Text(
                            text = "[${log.level}] ${log.message}",
                            color = color,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameNotInstalledDialog(
    game: VirtualGame,
    profile: DeviceProfile?,
    onDismiss: () -> Unit,
    onInstallFromPlayStore: () -> Unit,
    onPickApk: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, WarningAmber, RoundedCornerShape(20.dp)),
            color = VoidBlack
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GamingBadge(text = "GAME NOT INSTALLED", color = WarningAmber)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = game.appName,
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "${game.appName} is not installed on this physical device yet.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkCharcoal
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "To run this game with your ${profile?.model ?: "ASUS ROG Phone 8"} 144 FPS Profile, please install the real game onto your phone first:",
                            color = TextWhite,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onInstallFromPlayStore()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "INSTALL FROM GOOGLE PLAY", color = VoidBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onPickApk()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "INSTALL FROM APK FILE", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
