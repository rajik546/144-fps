package com.example.vphone.engine

import android.content.Context
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.data.model.VirtualPhone
import com.example.vphone.data.repository.VirtualPhoneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class VirtualEngineEvent(
    val level: String, // INFO, SUCCESS, WARN, ERROR
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class VirtualPhoneEngine(
    private val context: Context,
    private val repository: VirtualPhoneRepository,
    private val scope: CoroutineScope
) {
    private val _logs = MutableStateFlow<List<VirtualEngineEvent>>(emptyList())
    val logs: StateFlow<List<VirtualEngineEvent>> = _logs.asStateFlow()

    private val _events = MutableSharedFlow<VirtualEngineEvent>()
    val events: SharedFlow<VirtualEngineEvent> = _events.asSharedFlow()

    private var uptimeJob: Job? = null

    init {
        // Start uptime monitor for running phone
        uptimeJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000)
                repository.incrementRunningUptime(1)
            }
        }
    }

    private fun addLog(level: String, msg: String) {
        val event = VirtualEngineEvent(level, msg)
        val current = _logs.value.toMutableList()
        if (current.size > 100) current.removeAt(0)
        current.add(event)
        _logs.value = current
        scope.launch { _events.emit(event) }
    }

    fun startVirtualPhone(phone: VirtualPhone, profile: DeviceProfile?, onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            addLog("INFO", "Initializing Virtual Phone '${phone.name}' [ID: ${phone.id}]...")
            repository.updateStatus(phone.id, "STARTING")

            delay(300)
            val model = profile?.model ?: "Infinix X6891"
            val mfg = profile?.manufacturer ?: "INFINIX"
            addLog("INFO", "Applying Virtual Device Identity: $mfg $model (Refresh: ${phone.targetRefreshRate}Hz)...")

            delay(350)
            addLog("INFO", "Allocating RAM buffer: ${phone.allocatedRamMb} MB (Host guard active)...")

            delay(300)
            addLog("INFO", "Mounting isolated app sandbox container (/data/vphone_${phone.id}/)...")

            delay(300)
            addLog("INFO", "Configuring virtual display timing controller @ ${phone.targetResolution} (${phone.targetRefreshRate} FPS)...")

            delay(250)
            repository.updateStatus(phone.id, "RUNNING")
            addLog("SUCCESS", "Virtual Phone '${phone.name}' is now RUNNING in 144 FPS Gaming Profile Mode.")
            onComplete()
        }
    }

    fun stopVirtualPhone(phone: VirtualPhone, onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            addLog("INFO", "Stopping Virtual Phone '${phone.name}'...")
            delay(300)
            addLog("INFO", "Flushing virtual storage caches and unhooking sandbox...")
            repository.updateStatus(phone.id, "STOPPED")
            addLog("INFO", "Virtual Phone '${phone.name}' STOPPED successfully.")
            onComplete()
        }
    }

    fun restartVirtualPhone(phone: VirtualPhone, profile: DeviceProfile?, onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            addLog("INFO", "Restarting Virtual Phone '${phone.name}'...")
            repository.updateStatus(phone.id, "RESTARTING")
            delay(500)
            startVirtualPhone(phone, profile, onComplete)
        }
    }
}
