package com.example.vphone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.vphone.engine.FpsMeter
import com.example.vphone.engine.SystemResourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FloatingGamingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var params: WindowManager.LayoutParams? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var monitorJob: Job? = null
    private var isCompact = false
    private var overlayAlpha = 0.90f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(101, buildNotification())
        FpsMeter.start()

        if (Settings.canDrawOverlays(this)) {
            initOverlayView()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "144 FPS Gaming HUD Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors real-time gaming FPS, display refresh rate, and system resources"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("144 FPS Gaming HUD Active")
            .setContentText("Tracking real-time FPS, display refresh rate & telemetry")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun initOverlayView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 180
            alpha = overlayAlpha
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#E60D111A"))
            setPadding(28, 20, 28, 20)
            elevation = 16f
        }

        val fpsText = TextView(this).apply {
            text = "144 FPS"
            setTextColor(Color.parseColor("#00F0FF"))
            textSize = 20f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val telemetryText = TextView(this).apply {
            text = "HZ: 144 | RAM: -- | TEMP: --"
            setTextColor(Color.parseColor("#8F9BB3"))
            textSize = 11f
        }

        container.addView(fpsText)
        container.addView(telemetryText)

        // Make draggable and clickable to toggle compact mode
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = true

        container.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params?.x ?: 0
                    initialY = params?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isClick = false
                    }
                    params?.x = initialX + dx
                    params?.y = initialY + dy
                    params?.let { windowManager?.updateViewLayout(container, it) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        isCompact = !isCompact
                        telemetryText.visibility = if (isCompact) View.GONE else View.VISIBLE
                    }
                    true
                }
                else -> false
            }
        }

        overlayView = container
        params?.let { windowManager?.addView(container, it) }

        // Start polling updates
        val resourceMgr = SystemResourceManager(this)
        monitorJob = serviceScope.launch {
            while (isActive) {
                val fps = FpsMeter.fpsState.value.currentFps
                val stats = resourceMgr.getStats()
                fpsText.text = "${fps.roundToInt()} FPS"
                val profileTag = currentProfileModel.take(14)
                telemetryText.text = "[$profileTag] • 144Hz • RAM: ${stats.ramUsagePercent}%"
                delay(500)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(EXTRA_PROFILE_MODEL)?.let {
            currentProfileModel = it
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
        FpsMeter.stop()
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }

    companion object {
        const val CHANNEL_ID = "vphone_gaming_overlay_channel"
        const val EXTRA_PROFILE_MODEL = "extra_profile_model"
        var currentProfileModel: String = "ASUS ROG Phone 8"

        fun startService(context: Context, profileModel: String = "ASUS ROG Phone 8") {
            currentProfileModel = profileModel
            val intent = Intent(context, FloatingGamingOverlayService::class.java).apply {
                putExtra(EXTRA_PROFILE_MODEL, profileModel)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FloatingGamingOverlayService::class.java)
            context.stopService(intent)
        }
    }
}
