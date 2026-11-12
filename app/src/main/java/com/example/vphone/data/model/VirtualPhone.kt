package com.example.vphone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "virtual_phones")
data class VirtualPhone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val deviceProfileId: Long,
    val status: String = "STOPPED", // STOPPED, STARTING, RUNNING, RESTARTING
    val allocatedRamMb: Int = 4096,
    val allocatedStorageGb: Int = 32,
    val cpuCoresLimit: Int = 8,
    val targetResolution: String = "1080x2400",
    val targetRefreshRate: Int = 144,
    val graphicsMode: String = "High Performance", // Standard, Balanced, High Performance, High Compatibility
    val isolationMode: String = "Sandboxed Profile Container", // "AVF Hardware Hypervisor" or "Sandboxed Profile Container"
    val installedPackagesJson: String = "[]",
    val uptimeSeconds: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)
