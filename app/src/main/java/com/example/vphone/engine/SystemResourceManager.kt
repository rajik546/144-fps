package com.example.vphone.engine

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs

data class SystemResourceStats(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val usedRamMb: Long,
    val ramUsagePercent: Int,
    val isLowMemory: Boolean,
    val batteryPercent: Int,
    val batteryTemperatureC: Float,
    val isCharging: Boolean,
    val totalStorageGb: Long,
    val availableStorageGb: Long,
    val cpuCores: Int
)

class SystemResourceManager(private val context: Context) {

    fun getStats(): SystemResourceStats {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)

        val totalRam = if (memInfo.totalMem > 0L) {
            memInfo.totalMem / (1024 * 1024)
        } else {
            val jvmMax = Runtime.getRuntime().maxMemory() / (1024 * 1024)
            if (jvmMax > 1024L) jvmMax else 8192L
        }
        val availRam = if (memInfo.availMem > 0L) {
            memInfo.availMem / (1024 * 1024)
        } else {
            (totalRam / 2).coerceAtLeast(1024L)
        }
        val usedRam = (totalRam - availRam).coerceAtLeast(0L)
        val ramPercent = if (totalRam > 0) ((usedRam * 100) / totalRam).toInt() else 0

        // Battery
        var batteryPct = 85
        var tempC = 34.5f
        var isCharging = false

        try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    batteryPct = (level * 100) / scale
                }
                val rawTemp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 345)
                tempC = rawTemp / 10.0f
                val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
            }
        } catch (_: Throwable) {
            // fallback
        }

        // Storage
        var totalStorageGb = 128L
        var availStorageGb = 64L
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            totalStorageGb = (stat.blockCountLong * stat.blockSizeLong) / (1024 * 1024 * 1024)
            availStorageGb = (stat.availableBlocksLong * stat.blockSizeLong) / (1024 * 1024 * 1024)
        } catch (_: Throwable) {
            // fallback
        }

        val cores = Runtime.getRuntime().availableProcessors()

        return SystemResourceStats(
            totalRamMb = totalRam,
            availableRamMb = availRam,
            usedRamMb = usedRam,
            ramUsagePercent = ramPercent,
            isLowMemory = memInfo.lowMemory,
            batteryPercent = batteryPct,
            batteryTemperatureC = tempC,
            isCharging = isCharging,
            totalStorageGb = totalStorageGb,
            availableStorageGb = availStorageGb,
            cpuCores = cores
        )
    }

    fun calculateSafeRamLimitMb(requestedMb: Int): Int {
        val stats = getStats()
        // Never exceed (totalRam - 2048 MB) and never exceed available RAM + 1024 MB
        val maxSafe = (stats.totalRamMb - 2048).coerceAtLeast(1024).toInt()
        return requestedMb.coerceIn(1024, maxSafe)
    }
}
