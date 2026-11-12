package com.example.vphone.engine

import android.view.Choreographer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque

data class FpsReading(
    val currentFps: Double = 60.0,
    val frameTimeMs: Double = 16.67,
    val onePercentLowFps: Double = 55.0,
    val droppedFrames: Int = 0,
    val isTracking: Boolean = false
)

object FpsMeter : Choreographer.FrameCallback {

    private val _fpsState = MutableStateFlow(FpsReading())
    val fpsState: StateFlow<FpsReading> = _fpsState.asStateFlow()

    private var isRunning = false
    private var lastFrameTimeNanos: Long = 0L
    private val frameDurationsMs = ArrayDeque<Double>(120)
    private var droppedFramesCount = 0

    private var lastCalculationTime = 0L
    private var frameCountSinceLastCalc = 0

    fun start() {
        if (isRunning) return
        isRunning = true
        lastFrameTimeNanos = 0L
        frameDurationsMs.clear()
        droppedFramesCount = 0
        lastCalculationTime = System.currentTimeMillis()
        frameCountSinceLastCalc = 0
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun stop() {
        isRunning = false
        Choreographer.getInstance().removeFrameCallback(this)
        _fpsState.value = _fpsState.value.copy(isTracking = false)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        if (lastFrameTimeNanos > 0L) {
            val deltaNanos = frameTimeNanos - lastFrameTimeNanos
            val deltaMs = deltaNanos / 1_000_000.0

            if (deltaMs > 0.1 && deltaMs < 200.0) {
                if (frameDurationsMs.size >= 120) {
                    frameDurationsMs.removeFirst()
                }
                frameDurationsMs.addLast(deltaMs)

                // Detect frame drop (taking longer than 20ms in 60Hz mode or 10ms in 120Hz mode)
                if (deltaMs > 22.0) {
                    droppedFramesCount++
                }
            }

            frameCountSinceLastCalc++
            val now = System.currentTimeMillis()
            if (now - lastCalculationTime >= 400) {
                val durationSec = (now - lastCalculationTime) / 1000.0
                val calculatedFps = if (durationSec > 0) frameCountSinceLastCalc / durationSec else 60.0

                val avgFrameTimeMs = if (frameDurationsMs.isNotEmpty()) {
                    frameDurationsMs.average()
                } else {
                    16.67
                }

                // 1% low calculation
                val onePercentLow = if (frameDurationsMs.size >= 10) {
                    val sorted = frameDurationsMs.sortedDescending()
                    val p99Index = (sorted.size * 0.99).toInt().coerceAtMost(sorted.size - 1)
                    val worstFrameTimeMs = sorted[p99Index]
                    if (worstFrameTimeMs > 0) 1000.0 / worstFrameTimeMs else calculatedFps * 0.85
                } else {
                    calculatedFps * 0.90
                }

                _fpsState.value = FpsReading(
                    currentFps = calculatedFps.coerceIn(0.0, 240.0),
                    frameTimeMs = avgFrameTimeMs,
                    onePercentLowFps = onePercentLow.coerceIn(0.0, 240.0),
                    droppedFrames = droppedFramesCount,
                    isTracking = true
                )

                lastCalculationTime = now
                frameCountSinceLastCalc = 0
            }
        }

        lastFrameTimeNanos = frameTimeNanos
        if (isRunning) {
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
}
