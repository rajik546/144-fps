package com.example.vphone.ui.games

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.GamingBadge
import com.example.vphone.ui.components.GamingCard

@Composable
fun VirtualGamesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allGames by viewModel.allGames.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val runningPhone by viewModel.runningPhone.collectAsStateWithLifecycle()
    val allPhones by viewModel.allPhones.collectAsStateWithLifecycle()
    val selectedGame by viewModel.selectedGameForDetail.collectAsStateWithLifecycle()

    var showDetailDialog by remember { mutableStateOf(false) }
    var activeDetailGame by remember { mutableStateOf<VirtualGame?>(null) }

    // Check if dialog was requested from dashboard
    if (selectedGame != null && !showDetailDialog) {
        activeDetailGame = selectedGame
        showDetailDialog = true
        viewModel.selectGameForDetail(null)
    }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.showToast("APK selected: ${uri.lastPathSegment}. Parsing metadata and registering into Virtual Sandbox...")
        }
    }

    if (showDetailDialog && activeDetailGame != null) {
        val game = activeDetailGame!!
        val report = viewModel.getCompatibilityReport(game)
        GameDetailDialog(
            game = game,
            report = report,
            availableProfiles = allProfiles,
            onDismiss = { showDetailDialog = false },
            onSaveConfig = { fps, profileId ->
                viewModel.updateGameFps(game.packageName, fps)
                viewModel.updateGameProfile(game.packageName, profileId)
            },
            onLaunchGame = {
                showDetailDialog = false
                val targetPhone = runningPhone ?: allPhones.firstOrNull()
                if (targetPhone != null) {
                    viewModel.launchVirtualDesktop(targetPhone)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "VIRTUAL GAME LIBRARY",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Individual Game Profiles & 144 FPS Engine Config",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                GamingBadge(
                    text = "${allGames.size} GAMES",
                    color = NeonGreen
                )
            }
        }

        // Actions: Scan Installed Games & Import APK
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.scanDeviceGames(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("scan_device_games_button")
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "SCAN DEVICE", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") },
                    colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("import_apk_button")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "IMPORT APK", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(allGames) { game ->
            val profile = allProfiles.firstOrNull { it.id == game.assignedProfileId }
            VirtualGameItemCard(
                game = game,
                profileModel = profile?.model ?: "Infinix X6891",
                onConfigure = {
                    activeDetailGame = game
                    showDetailDialog = true
                },
                onLaunch = {
                    val targetPhone = runningPhone ?: allPhones.firstOrNull()
                    if (targetPhone != null) {
                        viewModel.launchVirtualDesktop(targetPhone)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun VirtualGameItemCard(
    game: VirtualGame,
    profileModel: String,
    onConfigure: () -> Unit,
    onLaunch: () -> Unit
) {
    GamingCard(
        modifier = Modifier.fillMaxWidth().testTag("game_item_card_${game.packageName}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricBlue, NeonCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.appName.take(2).uppercase(),
                            color = VoidBlack,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = game.appName,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = game.packageName,
                            color = TextSubtle,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                GamingBadge(
                    text = "${game.preferredFps} FPS",
                    color = if (game.preferredFps >= 120) NeonGreen else NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Profile: $profileModel",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Anti-Cheat Guard",
                        color = NeonGreen,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "LAUNCH IN V-PHONE", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = onConfigure,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "CONFIG", fontSize = 11.sp)
                }
            }
        }
    }
}
