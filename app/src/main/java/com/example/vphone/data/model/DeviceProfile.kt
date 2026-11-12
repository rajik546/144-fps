package com.example.vphone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_profiles")
data class DeviceProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String,
    val manufacturer: String,
    val brand: String,
    val model: String,
    val deviceName: String,
    val androidVersion: String = "Android 15 (API 35)",
    val screenResolution: String = "1080 x 2400 FHD+",
    val displayRefreshRate: Int = 144,
    val isDefault: Boolean = false,
    val notes: String = ""
)
