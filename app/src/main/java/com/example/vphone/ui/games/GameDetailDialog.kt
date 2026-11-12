package com.example.vphone.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardBorder
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
import com.example.vphone.engine.GameCompatibilityReport
import com.example.vphone.ui.components.GamingBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailDialog(
    game: VirtualGame,
    report: GameCompatibilityReport,
    availableProfiles: List<DeviceProfile>,
    onDismiss: () -> Unit,
    onSaveConfig: (preferredFps: Int, assignedProfileId: Long) -> Unit,
    onLaunchGame: () -> Unit
) {
    var preferredFps by remember { mutableIntStateOf(game.preferredFps) }
    var selectedProfileId by remember { mutableLongStateOf(game.assignedProfileId) }
    var profileDropdownExpanded by remember { mutableStateOf(false) }

    val currentProfile = availableProfiles.firstOrNull { it.id == selectedProfileId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = game.appName,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = game.packageName,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("game_dialog_close_button")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Compatibility Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ANTI-CHEAT & INTEGRITY CHECK",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Engine: ${report.antiCheatEngine}",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.antiCheatSafetySummary,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Native ABI: ${report.supportedAbi} • Play Integrity: ${report.integrityLevelExpected}",
                            color = TextSubtle,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preferred FPS options
                Text(text = "PREFERRED GAMING FPS TARGET", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(30, 60, 90, 120, 144).forEach { fps ->
                        val selected = (preferredFps == fps)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { preferredFps = fps }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${fps} FPS",
                                color = if (selected) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Device Profile selector
                Text(text = "ASSIGNED DEVICE PROFILE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = profileDropdownExpanded,
                    onExpandedChange = { profileDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = currentProfile?.profileName ?: "Select Profile",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = CardSurfaceVariant,
                            unfocusedContainerColor = CardSurfaceVariant
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth().testTag("game_profile_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = profileDropdownExpanded,
                        onDismissRequest = { profileDropdownExpanded = false },
                        modifier = Modifier.background(CardSurface)
                    ) {
                        availableProfiles.forEach { profile ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = profile.profileName, color = TextWhite, fontWeight = FontWeight.Bold)
                                        Text(text = "${profile.manufacturer} ${profile.model} (${profile.displayRefreshRate}Hz)", color = TextMuted, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedProfileId = profile.id
                                    profileDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recommendations
                Text(text = "144 FPS OPTIMIZATION GUIDE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCharcoal)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    report.recommendations.forEach { rec ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "•", color = NeonCyan, fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(text = rec, color = TextWhite, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onSaveConfig(preferredFps, selectedProfileId)
                            onLaunchGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("launch_game_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "LAUNCH IN V-PHONE", color = VoidBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = {
                            onSaveConfig(preferredFps, selectedProfileId)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "SAVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
