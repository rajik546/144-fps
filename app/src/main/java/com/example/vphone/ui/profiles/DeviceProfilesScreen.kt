package com.example.vphone.ui.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
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
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.GamingBadge
import com.example.vphone.ui.components.GamingCard

@Composable
fun DeviceProfilesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val runningPhone by viewModel.runningPhone.collectAsStateWithLifecycle()
    val allPhones by viewModel.allPhones.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<DeviceProfile?>(null) }

    if (showEditDialog) {
        DeviceProfileEditDialog(
            initialProfile = editingProfile,
            onDismiss = { showEditDialog = false },
            onSave = { profile ->
                viewModel.saveProfile(profile)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VoidBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingProfile = null
                    showEditDialog = true
                },
                containerColor = NeonCyan,
                contentColor = VoidBlack,
                shape = CircleShape,
                modifier = Modifier.testTag("create_profile_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Profile")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            text = "DEVICE PROFILES",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "BlueStacks 5 Custom Phone Identity Architecture",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    GamingBadge(
                        text = "${allProfiles.size} PROFILES",
                        color = NeonCyan
                    )
                }
            }

            // Technical Architecture Disclosure Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HOW DEVICE PROFILES FUNCTION",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Device profiles inject virtualized Build properties (MANUFACTURER, BRAND, MODEL, 144Hz timing) into the Sandboxed Guest Environment. Compatible games recognize high-refresh flagship hardware and unlock their 90 / 120 / 144 FPS menus without root exploits or modifying host Android OS.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(allProfiles) { profile ->
                val isDefault = profile.isDefault
                val isAppliedToActivePhone = (runningPhone?.deviceProfileId == profile.id) ||
                        (runningPhone == null && allPhones.firstOrNull()?.deviceProfileId == profile.id)

                DeviceProfileItemCard(
                    profile = profile,
                    isDefault = isDefault,
                    isAppliedToActivePhone = isAppliedToActivePhone,
                    onEdit = {
                        editingProfile = profile
                        showEditDialog = true
                    },
                    onDuplicate = { viewModel.duplicateProfile(profile) },
                    onDelete = { viewModel.deleteProfile(profile) },
                    onSetDefault = { viewModel.setDefaultProfile(profile) },
                    onApplyToPhone = {
                        val targetPhone = runningPhone ?: allPhones.firstOrNull()
                        if (targetPhone != null) {
                            viewModel.savePhone(targetPhone.copy(deviceProfileId = profile.id, targetRefreshRate = profile.displayRefreshRate))
                            viewModel.showToast("Applied '${profile.profileName}' to '${targetPhone.name}'")
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DeviceProfileItemCard(
    profile: DeviceProfile,
    isDefault: Boolean,
    isAppliedToActivePhone: Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
    onApplyToPhone: () -> Unit
) {
    GamingCard(
        borderColor = if (isAppliedToActivePhone) NeonCyan else CardBorder,
        modifier = Modifier.fillMaxWidth().testTag("device_profile_card_${profile.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isAppliedToActivePhone) ElectricBlue else DarkCharcoal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = if (isAppliedToActivePhone) Color.White else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.profileName,
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isDefault) {
                                Spacer(modifier = Modifier.width(6.dp))
                                GamingBadge(text = "DEFAULT", color = WarningAmber)
                            }
                        }
                        Text(
                            text = "${profile.manufacturer} • ${profile.model}",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                GamingBadge(
                    text = "${profile.displayRefreshRate} Hz",
                    color = if (profile.displayRefreshRate >= 144) NeonGreen else ElectricBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "BRAND / MODEL", color = TextSubtle, fontSize = 9.sp)
                    Text(text = "${profile.brand} / ${profile.deviceName}", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Column {
                    Text(text = "RESOLUTION", color = TextSubtle, fontSize = 9.sp)
                    Text(text = profile.screenResolution, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Column {
                    Text(text = "OS VERSION", color = TextSubtle, fontSize = 9.sp)
                    Text(text = profile.androidVersion.take(10), color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (profile.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profile.notes,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onApplyToPhone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAppliedToActivePhone) CardSurfaceVariant else NeonCyan
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isAppliedToActivePhone) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "APPLIED TO V-PHONE", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(text = "APPLY TO V-PHONE", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSetDefault, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isDefault) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Set Default",
                            tint = if (isDefault) WarningAmber else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                    if (!isDefault) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CyberPink, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
