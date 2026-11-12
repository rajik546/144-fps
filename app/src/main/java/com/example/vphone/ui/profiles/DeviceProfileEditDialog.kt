package com.example.vphone.ui.profiles

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.vphone.data.model.DeviceProfile

@Composable
fun DeviceProfileEditDialog(
    initialProfile: DeviceProfile?,
    onDismiss: () -> Unit,
    onSave: (DeviceProfile) -> Unit
) {
    var profileName by remember { mutableStateOf(initialProfile?.profileName ?: "Custom Gaming Profile") }
    var manufacturer by remember { mutableStateOf(initialProfile?.manufacturer ?: "ASUS") }
    var brand by remember { mutableStateOf(initialProfile?.brand ?: "ASUS") }
    var model by remember { mutableStateOf(initialProfile?.model ?: "ROG Phone 8") }
    var deviceName by remember { mutableStateOf(initialProfile?.deviceName ?: "ASUS_AI2401") }
    var androidVersion by remember { mutableStateOf(initialProfile?.androidVersion ?: "Android 15 (API 35)") }
    var screenResolution by remember { mutableStateOf(initialProfile?.screenResolution ?: "1080 x 2400 FHD+") }
    var refreshRate by remember { mutableIntStateOf(initialProfile?.displayRefreshRate ?: 144) }
    var notes by remember { mutableStateOf(initialProfile?.notes ?: "Custom 144 FPS phone profile") }

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
                            text = if (initialProfile == null) "CREATE DEVICE PROFILE" else "EDIT PHONE PROFILE",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "BlueStacks 5-Style Device Identity Customization",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("profile_dialog_close")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Presets Quick-Fill Row
                Text(text = "QUICK PRESETS", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Infinix X6891", "ROG Phone 8", "S24 Ultra", "K80 Ultra").forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    when (preset) {
                                        "Infinix X6891" -> {
                                            profileName = "Infinix X6891 Native"
                                            manufacturer = "INFINIX"
                                            brand = "INFINIX"
                                            model = "Infinix X6891"
                                            deviceName = "Infinix-X6891"
                                            refreshRate = 144
                                            notes = "Native Infinix hardware identity."
                                        }
                                        "ROG Phone 8" -> {
                                            profileName = "ASUS ROG Phone 8 (144Hz)"
                                            manufacturer = "ASUS"
                                            brand = "ASUS"
                                            model = "ROG Phone 8"
                                            deviceName = "ASUS_AI2401"
                                            refreshRate = 144
                                            notes = "Unlocks 120-144 FPS in PUBG and Free Fire MAX."
                                        }
                                        "S24 Ultra" -> {
                                            profileName = "Samsung Galaxy S24 Ultra"
                                            manufacturer = "Samsung"
                                            brand = "Samsung"
                                            model = "Galaxy S24 Ultra"
                                            deviceName = "SM-S928B"
                                            refreshRate = 120
                                            notes = "Flagship Snapdragon profile."
                                        }
                                        "K80 Ultra" -> {
                                            profileName = "Xiaomi Redmi K80 Ultra"
                                            manufacturer = "Xiaomi"
                                            brand = "Xiaomi"
                                            model = "Redmi K80 Ultra"
                                            deviceName = "24122RKC7C"
                                            refreshRate = 144
                                            notes = "Esports high refresh rate gaming."
                                        }
                                    }
                                },
                            color = CardSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(
                                text = preset,
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Name
                ProfileInputField(label = "PROFILE DISPLAY NAME", value = profileName, onValueChange = { profileName = it })
                Spacer(modifier = Modifier.height(10.dp))

                // Manufacturer & Brand
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputField(label = "MANUFACTURER", value = manufacturer, onValueChange = { manufacturer = it })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputField(label = "BRAND", value = brand, onValueChange = { brand = it })
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Model & Device Name
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputField(label = "MODEL", value = model, onValueChange = { model = it })
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputField(label = "DEVICE CODENAME", value = deviceName, onValueChange = { deviceName = it })
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Android Version
                ProfileInputField(label = "ANDROID VERSION PREFERENCE", value = androidVersion, onValueChange = { androidVersion = it })
                Spacer(modifier = Modifier.height(10.dp))

                // Resolution
                ProfileInputField(label = "SCREEN RESOLUTION", value = screenResolution, onValueChange = { screenResolution = it })
                Spacer(modifier = Modifier.height(14.dp))

                // Refresh Rate Selector
                Text(text = "DISPLAY REFRESH RATE PREFERENCE", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(60, 90, 120, 144).forEach { hz ->
                        val selected = (refreshRate == hz)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) ElectricBlue else CardSurfaceVariant)
                                .border(1.dp, if (selected) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { refreshRate = hz }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${hz}Hz", color = if (selected) Color.White else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes
                ProfileInputField(label = "PROFILE NOTES", value = notes, onValueChange = { notes = it })

                Spacer(modifier = Modifier.height(20.dp))

                // Save
                Button(
                    onClick = {
                        val updated = initialProfile?.copy(
                            profileName = profileName,
                            manufacturer = manufacturer,
                            brand = brand,
                            model = model,
                            deviceName = deviceName,
                            androidVersion = androidVersion,
                            screenResolution = screenResolution,
                            displayRefreshRate = refreshRate,
                            notes = notes
                        ) ?: DeviceProfile(
                            id = 0,
                            profileName = profileName,
                            manufacturer = manufacturer,
                            brand = brand,
                            model = model,
                            deviceName = deviceName,
                            androidVersion = androidVersion,
                            screenResolution = screenResolution,
                            displayRefreshRate = refreshRate,
                            notes = notes
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = VoidBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "SAVE PROFILE", color = VoidBlack, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = CardSurfaceVariant,
                unfocusedContainerColor = CardSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
