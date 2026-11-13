package com.example.vphone.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vphone.data.db.AppDatabase
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.data.model.VirtualGame
import com.example.vphone.data.model.VirtualPhone
import com.example.vphone.data.repository.DeviceProfileRepository
import com.example.vphone.data.repository.VirtualGameRepository
import com.example.vphone.data.repository.VirtualPhoneRepository
import com.example.vphone.engine.DisplayCapabilities
import com.example.vphone.engine.DisplayRefreshRateManager
import com.example.vphone.engine.FeasibilityReport
import com.example.vphone.engine.FpsMeter
import com.example.vphone.engine.FpsReading
import com.example.vphone.engine.GameCompatibilityChecker
import com.example.vphone.engine.GameCompatibilityReport
import com.example.vphone.engine.GpuDiagnostics
import com.example.vphone.engine.GpuReport
import com.example.vphone.engine.SystemResourceManager
import com.example.vphone.engine.SystemResourceStats
import com.example.vphone.engine.VirtualPhoneEngine
import com.example.vphone.engine.VirtualizationFeasibilityEngine
import com.example.vphone.service.FloatingGamingOverlayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val profileRepo = DeviceProfileRepository(database.deviceProfileDao())
    private val phoneRepo = VirtualPhoneRepository(database.virtualPhoneDao())
    private val gameRepo = VirtualGameRepository(database.virtualGameDao())

    val feasibilityEngine = VirtualizationFeasibilityEngine(application)
    val displayManager = DisplayRefreshRateManager(application)
    val gpuDiagnostics = GpuDiagnostics(application)
    val resourceManager = SystemResourceManager(application)
    val compatibilityChecker = GameCompatibilityChecker(application)
    val virtualEngine = VirtualPhoneEngine(application, phoneRepo, viewModelScope)

    // Data streams
    val allProfiles: StateFlow<List<DeviceProfile>> = profileRepo.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPhones: StateFlow<List<VirtualPhone>> = phoneRepo.allVirtualPhones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val runningPhone: StateFlow<VirtualPhone?> = phoneRepo.runningPhone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allGames: StateFlow<List<VirtualGame>> = gameRepo.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hardware & Engine state
    private val _feasibility = MutableStateFlow(feasibilityEngine.runInvestigation())
    val feasibility: StateFlow<FeasibilityReport> = _feasibility.asStateFlow()

    private val _displayCaps = MutableStateFlow(displayManager.getDisplayCapabilities())
    val displayCaps: StateFlow<DisplayCapabilities> = _displayCaps.asStateFlow()

    private val _gpuReport = MutableStateFlow(gpuDiagnostics.getGpuReport("High Performance"))
    val gpuReport: StateFlow<GpuReport> = _gpuReport.asStateFlow()

    private val _systemStats = MutableStateFlow(resourceManager.getStats())
    val systemStats: StateFlow<SystemResourceStats> = _systemStats.asStateFlow()

    val fpsReading: StateFlow<FpsReading> = FpsMeter.fpsState

    // UI state
    private val _showFloatingHud = MutableStateFlow(false)
    val showFloatingHud: StateFlow<Boolean> = _showFloatingHud.asStateFlow()

    private val _activeDesktopPhone = MutableStateFlow<VirtualPhone?>(null)
    val activeDesktopPhone: StateFlow<VirtualPhone?> = _activeDesktopPhone.asStateFlow()

    private val _selectedGameForDetail = MutableStateFlow<VirtualGame?>(null)
    val selectedGameForDetail: StateFlow<VirtualGame?> = _selectedGameForDetail.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    private val _installedPackages = MutableStateFlow<Set<String>>(emptySet())
    val installedPackages: StateFlow<Set<String>> = _installedPackages.asStateFlow()

    init {
        FpsMeter.start()
        refreshInstalledPackages(application)

        // Background resource ticker
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                _systemStats.value = resourceManager.getStats()
                delay(1500)
            }
        }
    }

    fun showToast(msg: String) {
        _notificationMessage.value = msg
    }

    fun clearToast() {
        _notificationMessage.value = null
    }

    fun refreshHardwareState(activity: Activity? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _feasibility.value = feasibilityEngine.runInvestigation()
            _displayCaps.value = displayManager.getDisplayCapabilities(activity)
            _gpuReport.value = gpuDiagnostics.getGpuReport()
            _systemStats.value = resourceManager.getStats()
        }
    }

    fun lockMaxRefreshRate(activity: Activity?) {
        val maxHz = _displayCaps.value.maxSupportedRefreshRate
        val success = displayManager.applyTargetRefreshRate(activity?.window, maxHz)
        if (success) {
            showToast("Applied $maxHz Hz Display Mode to Window")
            refreshHardwareState(activity)
        } else {
            showToast("Failed to apply $maxHz Hz display mode")
        }
    }

    fun applyRefreshRate(activity: Activity?, targetHz: Int) {
        val success = displayManager.applyTargetRefreshRate(activity?.window, targetHz)
        if (success) {
            showToast("Target $targetHz Hz applied to display mode")
            refreshHardwareState(activity)
        } else {
            showToast("Could not set $targetHz Hz display mode")
        }
    }

    // Virtual Phone Actions
    fun startPhone(phone: VirtualPhone) {
        val profile = allProfiles.value.firstOrNull { it.id == phone.deviceProfileId }
        virtualEngine.startVirtualPhone(phone, profile) {
            showToast("Virtual Phone '${phone.name}' started successfully")
        }
    }

    fun stopPhone(phone: VirtualPhone) {
        virtualEngine.stopVirtualPhone(phone) {
            showToast("Virtual Phone '${phone.name}' stopped")
            if (_activeDesktopPhone.value?.id == phone.id) {
                _activeDesktopPhone.value = null
            }
        }
    }

    fun restartPhone(phone: VirtualPhone) {
        val profile = allProfiles.value.firstOrNull { it.id == phone.deviceProfileId }
        virtualEngine.restartVirtualPhone(phone, profile) {
            showToast("Virtual Phone '${phone.name}' restarted")
        }
    }

    fun savePhone(phone: VirtualPhone) {
        viewModelScope.launch {
            phoneRepo.saveVirtualPhone(phone)
            showToast("Virtual Phone saved")
        }
    }

    fun duplicatePhone(phone: VirtualPhone) {
        viewModelScope.launch {
            phoneRepo.duplicateVirtualPhone(phone)
            showToast("Virtual Phone duplicated")
        }
    }

    fun deletePhone(phone: VirtualPhone) {
        viewModelScope.launch {
            phoneRepo.deleteVirtualPhone(phone)
            showToast("Virtual Phone deleted")
        }
    }

    fun launchVirtualDesktop(phone: VirtualPhone) {
        if (phone.status != "RUNNING") {
            startPhone(phone)
        }
        _activeDesktopPhone.value = phone
    }

    fun exitVirtualDesktop() {
        _activeDesktopPhone.value = null
    }

    // Device Profile Actions
    fun saveProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            profileRepo.saveProfile(profile)
            showToast("Device Profile '${profile.profileName}' saved")
        }
    }

    fun duplicateProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            profileRepo.duplicateProfile(profile)
            showToast("Profile duplicated")
        }
    }

    fun deleteProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            profileRepo.deleteProfile(profile)
            showToast("Profile deleted")
        }
    }

    fun setDefaultProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            profileRepo.setDefaultProfile(profile.id)
            showToast("Set '${profile.profileName}' as Default Profile")
        }
    }

    // Game Actions
    fun updateGameFps(packageName: String, fps: Int) {
        viewModelScope.launch {
            gameRepo.updateFps(packageName, fps)
            showToast("Target FPS for $packageName set to $fps FPS")
        }
    }

    fun updateGameProfile(packageName: String, profileId: Long) {
        viewModelScope.launch {
            gameRepo.updateAssignedProfile(packageName, profileId)
            showToast("Device Profile updated for game")
        }
    }

    fun selectGameForDetail(game: VirtualGame?) {
        _selectedGameForDetail.value = game
    }

    fun getCompatibilityReport(game: VirtualGame): GameCompatibilityReport {
        val profile = allProfiles.value.firstOrNull { it.id == game.assignedProfileId }
        val model = profile?.model ?: "Infinix X6891"
        return compatibilityChecker.analyzeGame(game, model)
    }

    fun toggleFloatingHud() {
        _showFloatingHud.value = !_showFloatingHud.value
    }

    fun startSystemOverlayService(context: Context) {
        if (Settings.canDrawOverlays(context)) {
            FloatingGamingOverlayService.startService(context)
            showToast("System Floating Gaming HUD started")
        } else {
            showToast("Overlay permission required. Opening system settings...")
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }

    fun stopSystemOverlayService(context: Context) {
        FloatingGamingOverlayService.stopService(context)
        showToast("System Floating Gaming HUD stopped")
    }

    fun scanDeviceGames(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val detectedGames = mutableListOf<VirtualGame>()

            for (app in installedApps) {
                // Ignore system packages unless target gaming
                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val pkg = app.packageName
                val isTarget = pkg.contains("freefire") || pkg.contains("pubg") ||
                        pkg.contains("imobile") || pkg.contains("callofduty") ||
                        pkg.contains("genshin") || pkg.contains("gameloft") ||
                        pkg.contains("riotgames") || app.category == ApplicationInfo.CATEGORY_GAME

                if (isTarget && (!isSystem || pkg.contains("game") || pkg.contains("dts"))) {
                    val label = pm.getApplicationLabel(app).toString()
                    val pInfo = try { pm.getPackageInfo(pkg, 0) } catch (_: Throwable) { null }
                    detectedGames.add(
                        VirtualGame(
                            packageName = pkg,
                            appName = label,
                            versionName = pInfo?.versionName ?: "1.0",
                            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo?.longVersionCode ?: 1L else @Suppress("DEPRECATION") (pInfo?.versionCode?.toLong() ?: 1L),
                            installedInVirtualPhoneId = 1,
                            preferredFps = 144,
                            assignedProfileId = 2,
                            graphicsMode = "High Performance",
                            isAddedToVirtualEnv = true,
                            antiCheatEngine = if (pkg.contains("freefire")) "Garena HackShield" else if (pkg.contains("pubg") || pkg.contains("tencent")) "Tencent ACE / MTP" else "Standard Android Integrity",
                            playIntegrityRequirement = "MEETS_DEVICE_INTEGRITY",
                            playServicesRequired = true,
                            nativeAbi = "arm64-v8a",
                            compatibilityStatus = "Detected on Device (Optimized for 144 FPS)",
                            isOfficialGame = true
                        )
                    )
                }
            }

            if (detectedGames.isNotEmpty()) {
                gameRepo.insertAll(detectedGames)
                showToast("Detected and synced ${detectedGames.size} game(s) to Virtual Library")
            } else {
                showToast("Scan complete: No additional games detected on device.")
            }
            refreshInstalledPackages(context)
        }
    }

    fun isGameInstalled(packageName: String): Boolean {
        return _installedPackages.value.contains(packageName)
    }

    fun refreshInstalledPackages(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val pkgs = try {
                pm.getInstalledApplications(0).map { it.packageName }.toSet()
            } catch (_: Throwable) {
                emptySet()
            }
            _installedPackages.value = pkgs
        }
    }

    fun launchRealGame(context: Context, game: VirtualGame, targetHz: Int? = null) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(game.packageName)

        if (launchIntent != null) {
            val assignedProfile = allProfiles.value.firstOrNull { it.id == game.assignedProfileId }
            val profileModel = assignedProfile?.model ?: "ASUS ROG Phone 8"
            val hz = targetHz ?: game.preferredFps

            if (context is Activity) {
                displayManager.applyTargetRefreshRate(context.window, hz)
            }

            if (Settings.canDrawOverlays(context)) {
                FloatingGamingOverlayService.startService(context, profileModel)
            } else {
                _showFloatingHud.value = true
            }

            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            try {
                context.startActivity(launchIntent)
                showToast("Launching real ${game.appName} with $profileModel 144Hz Gaming Profile & HUD...")
            } catch (e: Exception) {
                showToast("Failed to launch ${game.appName}: ${e.message}")
            }
        } else {
            showToast("${game.appName} is not installed on this device. Opening Google Play Store to install...")
            openPlayStore(context, game.packageName)
        }
    }

    fun openPlayStore(context: Context, packageName: String) {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (_: Throwable) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (e: Throwable) {
                showToast("Could not open Play Store: ${e.message}")
            }
        }
    }

    fun installApk(context: Context, apkUri: Uri) {
        try {
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
            showToast("Opening package installer for selected APK...")
        } catch (e: Exception) {
            showToast("Failed to launch package installer: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        FpsMeter.stop()
    }
}
