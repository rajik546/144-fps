package com.example.vphone.ui.phones

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.WarningAmber
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.data.model.VirtualPhone
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualPhoneDetailDialog(
    initialPhone: VirtualPhone?,
    availableProfiles: List<DeviceProfile>,
    maxSafeRamMb: Long,
    onDismiss: () -> Unit,
    onSave: (VirtualPhone) -> Unit
) {
    var name by remember { mutableStateOf(initialPhone?.name ?: "New Virtual Phone") }
    var selectedProfileId by remember {
        mutableLongStateOf(initialPhone?.deviceProfileId ?: (availableProfiles.firstOrNull()?.id ?: 1L))
    }
    var ramAllocationMb by remember {
        mutableIntStateOf(initialPhone?.allocatedRamMb ?: 4096)
    }
    var storageAllocationGb by remember {
        mutableIntStateOf(initialPhone?.allocatedStorageGb ?: 32)
    }
    var targetHz by remember {
        mutableIntStateOf(initialPhone?.targetRefreshRate ?: 144)
    }
    var targetResolution by remember {
        mutableStateOf(initialPhone?.targetResolution ?: "1080x2400")
    }
    var graphicsMode by remember {
        mutableStateOf(initialPhone?.graphicsMode ?: "High Performance")
    }

    var profileDropdownExpanded by remember { mutableStateOf(false) }
    var hzDropdownExpanded by remember { mutableStateOf(false) }
    var resDropdownExpanded by remember { mutableStateOf(false) }

    val currentProfileName = availableProfiles.firstOrNull { it.id == selectedProfileId }?.profileName
        ?: "Select Device Profile"

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
                            text = if (initialPhone == null) "CREATE VIRTUAL PHONE" else "CONFIGURE V-PHONE",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "BlueStacks-Style Multi-Instance & Specs Config",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_close_button")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name field
                Text(text = "VIRTUAL PHONE NAME", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardSurfaceVariant,
                        unfocusedContainerColor = CardSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("vphone_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Device Profile Dropdown
                Text(text = "DEVICE IDENTITY PROFILE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = profileDropdownExpanded,
                    onExpandedChange = { profileDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = currentProfileName,
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
                        modifier = Modifier.menuAnchor().fillMaxWidth().testTag("profile_dropdown_anchor")
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
                                        Text(text = "${profile.manufacturer} ${profile.model} • ${profile.displayRefreshRate}Hz", color = TextMuted, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedProfileId = profile.id
                                    targetHz = profile.displayRefreshRate
                                    profileDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RAM Allocation with Safe Host Guard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "RAM ALLOCATION", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$ramAllocationMb MB", color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = ramAllocationMb.toFloat(),
                    onValueChange = { ramAllocationMb = ((it / 512).roundToInt() * 512).coerceIn(1024, maxSafeRamMb.toInt()) },
                    valueRange = 1024f..maxSafeRamMb.toFloat(),
                    steps = ((maxSafeRamMb - 1024) / 512).toInt().coerceAtLeast(1),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = CardBorder
                    ),
                    modifier = Modifier.testTag("ram_slider")
                )
                Text(
                    text = "Host Stability Guard: Reserves >= 2048 MB RAM for physical Android OS.",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Storage Allocation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "VIRTUAL STORAGE QUOTA", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$storageAllocationGb GB", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = storageAllocationGb.toFloat(),
                    onValueChange = { storageAllocationGb = it.roundToInt() },
                    valueRange = 16f..128f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGreen,
                        activeTrackColor = NeonGreen,
                        inactiveTrackColor = CardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Target Refresh Rate
                Text(text = "TARGET DISPLAY REFRESH RATE", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(60, 90, 120, 144).forEach { hz ->
                        val selected = (targetHz == hz)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { targetHz = hz }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hz}Hz",
                                color = if (selected) Color.White else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resolution
                Text(text = "RESOLUTION", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("720x1600", "1080x2400", "1440x3120").forEach { res ->
                        val selected = (targetResolution == res)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { targetResolution = res }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (res.contains("720")) "HD+" else if (res.contains("1080")) "FHD+" else "QHD+",
                                color = if (selected) Color.White else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = {
                        val updated = initialPhone?.copy(
                            name = name,
                            deviceProfileId = selectedProfileId,
                            allocatedRamMb = ramAllocationMb,
                            allocatedStorageGb = storageAllocationGb,
                            targetRefreshRate = targetHz,
                            targetResolution = targetResolution,
                            graphicsMode = graphicsMode
                        ) ?: VirtualPhone(
                            id = 0,
                            name = name,
                            deviceProfileId = selectedProfileId,
                            allocatedRamMb = ramAllocationMb,
                            allocatedStorageGb = storageAllocationGb,
                            targetRefreshRate = targetHz,
                            targetResolution = targetResolution,
                            graphicsMode = graphicsMode,
                            status = "STOPPED"
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_vphone_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = VoidBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE VIRTUAL PHONE",
                        color = VoidBlack,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
