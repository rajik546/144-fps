package com.example.vphone.engine

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import android.view.Window
import android.view.WindowManager
import kotlin.math.roundToInt

data class DisplayModeInfo(
    val modeId: Int,
    val width: Int,
    val height: Int,
    val refreshRate: Float,
    val refreshRateInt: Int,
    val isCurrent: Boolean
)

data class DisplayCapabilities(
    val currentRefreshRate: Float,
    val currentRefreshRateInt: Int,
    val maxSupportedRefreshRate: Int,
    val supports144Hz: Boolean,
    val supports120Hz: Boolean,
    val supports90Hz: Boolean,
    val supportedModes: List<DisplayModeInfo>,
    val preferredFpsOptions: List<Int> // [30, 40, 45, 60, 90, 120, 144] with capability tag
)

class DisplayRefreshRateManager(private val context: Context) {

    fun getDisplayCapabilities(activity: Activity? = null): DisplayCapabilities {
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.display ?: run {
                val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
                displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
            }
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
        }

        val currentMode = display?.mode
        val currentHz = currentMode?.refreshRate ?: 60.0f
        val currentHzInt = currentHz.roundToInt()

        val supportedModes = mutableListOf<DisplayModeInfo>()
        var maxHz = currentHzInt

        display?.supportedModes?.forEach { mode ->
            val hz = mode.refreshRate
            val hzInt = hz.roundToInt()
            if (hzInt > maxHz) {
                maxHz = hzInt
            }
            supportedModes.add(
                DisplayModeInfo(
                    modeId = mode.modeId,
                    width = mode.physicalWidth,
                    height = mode.physicalHeight,
                    refreshRate = hz,
                    refreshRateInt = hzInt,
                    isCurrent = (currentMode != null && mode.modeId == currentMode.modeId)
                )
            )
        }

        // Distinct by refresh rate and resolution
        val sortedModes = supportedModes.sortedByDescending { it.refreshRate }

        val supports144 = maxHz >= 144 || sortedModes.any { it.refreshRateInt >= 144 }
        val supports120 = maxHz >= 120 || sortedModes.any { it.refreshRateInt >= 120 }
        val supports90 = maxHz >= 90 || sortedModes.any { it.refreshRateInt >= 90 }

        val allOptions = listOf(30, 40, 45, 60, 90, 120, 144)

        return DisplayCapabilities(
            currentRefreshRate = currentHz,
            currentRefreshRateInt = currentHzInt,
            maxSupportedRefreshRate = maxHz,
            supports144Hz = supports144,
            supports120Hz = supports120,
            supports90Hz = supports90,
            supportedModes = sortedModes,
            preferredFpsOptions = allOptions
        )
    }

    /**
     * Request Android system window to set highest display refresh rate or specific target refresh rate
     */
    fun applyTargetRefreshRate(window: Window?, targetHz: Int): Boolean {
        if (window == null) return false
        return try {
            val params = window.attributes
            if (targetHz > 0) {
                params.preferredRefreshRate = targetHz.toFloat()
            }
            // Find mode with matching or highest refresh rate
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.context.display
            } else {
                @Suppress("DEPRECATION")
                window.windowManager.defaultDisplay
            }

            display?.supportedModes?.let { modes ->
                val matchingMode = modes
                    .filter { it.refreshRate.roundToInt() == targetHz }
                    .maxByOrNull { it.physicalWidth * it.physicalHeight }
                    ?: modes.maxByOrNull { it.refreshRate }

                if (matchingMode != null) {
                    params.preferredDisplayModeId = matchingMode.modeId
                }
            }

            window.attributes = params
            true
        } catch (_: Throwable) {
            false
        }
    }
}
