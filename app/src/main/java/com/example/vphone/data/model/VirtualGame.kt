package com.example.vphone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "virtual_games")
data class VirtualGame(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val versionName: String = "1.0",
    val versionCode: Long = 1,
    val installedInVirtualPhoneId: Long = 1,
    val preferredFps: Int = 144, // 30, 40, 45, 60, 90, 120, 144
    val assignedProfileId: Long = 1,
    val graphicsMode: String = "High Performance",
    val isAddedToVirtualEnv: Boolean = true,
    val antiCheatEngine: String = "Tencent ACE / MTP",
    val playIntegrityRequirement: String = "MEETS_DEVICE_INTEGRITY",
    val playServicesRequired: Boolean = true,
    val nativeAbi: String = "arm64-v8a",
    val compatibilityStatus: String = "Compatible (Sandboxed Mode)",
    val isOfficialGame: Boolean = true
)
