package com.example.vphone.ui.phones

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
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
import com.example.vphone.data.model.VirtualPhone
import com.example.vphone.ui.MainViewModel
import com.example.vphone.ui.components.GamingBadge
import com.example.vphone.ui.components.GamingCard
import com.example.vphone.ui.components.StatusIndicatorPill

@Composable
fun VirtualPhonesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allPhones by viewModel.allPhones.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val feasibility by viewModel.feasibility.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingPhone by remember { mutableStateOf<VirtualPhone?>(null) }

    if (showEditDialog) {
        VirtualPhoneDetailDialog(
            initialPhone = editingPhone,
            availableProfiles = allProfiles,
            maxSafeRamMb = feasibility.safeAllocatableRamMb,
            onDismiss = { showEditDialog = false },
            onSave = { phone ->
                viewModel.savePhone(phone)
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
                    editingPhone = null
                    showEditDialog = true
                },
                containerColor = NeonCyan,
                contentColor = VoidBlack,
                shape = CircleShape,
                modifier = Modifier.testTag("create_vphone_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Virtual Phone")
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
                            text = "VIRTUAL PHONES",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Android Virtual Device Manager & Multi-Instance Rig",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    GamingBadge(
                        text = "${allPhones.size} INSTANCES",
                        color = ElectricBlue
                    )
                }
            }

            // Architecture Mode Note
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Instances run in isolated Sandboxed Profile Containers with independent RAM allocation and 144Hz display sync. Safe from anti-cheat bans.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            items(allPhones) { phone ->
                val profile = allProfiles.firstOrNull { it.id == phone.deviceProfileId }
                VirtualPhoneItemCard(
                    phone = phone,
                    profileName = profile?.profileName ?: "Infinix X6891",
                    profileModel = profile?.model ?: "X6891",
                    onStart = { viewModel.startPhone(phone) },
                    onStop = { viewModel.stopPhone(phone) },
                    onRestart = { viewModel.restartPhone(phone) },
                    onOpenDesktop = { viewModel.launchVirtualDesktop(phone) },
                    onConfigure = {
                        editingPhone = phone
                        showEditDialog = true
                    },
                    onDuplicate = { viewModel.duplicatePhone(phone) },
                    onDelete = { viewModel.deletePhone(phone) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun VirtualPhoneItemCard(
    phone: VirtualPhone,
    profileName: String,
    profileModel: String,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit,
    onOpenDesktop: () -> Unit,
    onConfigure: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val isRunning = phone.status == "RUNNING"

    GamingCard(
        borderColor = if (isRunning) CardBorderGlow else CardBorder,
        modifier = Modifier.fillMaxWidth().testTag("vphone_card_${phone.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
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
                            .background(if (isRunning) NeonCyan.copy(alpha = 0.2f) else DarkCharcoal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = if (isRunning) NeonCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = phone.name,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Identity: $profileModel",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                StatusIndicatorPill(status = phone.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "REFRESH TARGET", color = TextSubtle, fontSize = 9.sp)
                    Text(text = "${phone.targetRefreshRate} Hz", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "RAM BUFFER", color = TextSubtle, fontSize = 9.sp)
                    Text(text = "${phone.allocatedRamMb} MB", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "RESOLUTION", color = TextSubtle, fontSize = 9.sp)
                    Text(text = phone.targetResolution, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "GRAPHICS", color = TextSubtle, fontSize = 9.sp)
                    Text(text = phone.graphicsMode.take(8), color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenDesktop,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("open_desktop_button_${phone.id}")
                ) {
                    Icon(imageVector = Icons.Default.Tv, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "V-DESKTOP", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }

                if (isRunning) {
                    OutlinedButton(
                        onClick = onStop,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, tint = CyberPink, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "STOP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VoidBlack, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "START", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(onClick = onRestart, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart", tint = TextMuted, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onConfigure, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Configure", tint = TextMuted, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDuplicate, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextMuted, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CyberPink, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
