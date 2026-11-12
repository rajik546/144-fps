package com.example.vphone.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vphone.data.model.DeviceProfile
import com.example.vphone.data.model.VirtualGame
import com.example.vphone.data.model.VirtualPhone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DeviceProfile::class, VirtualPhone::class, VirtualGame::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceProfileDao(): DeviceProfileDao
    abstract fun virtualPhoneDao(): VirtualPhoneDao
    abstract fun virtualGameDao(): VirtualGameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vphone_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedDefaultData(database)
                }
            }
        }

        private suspend fun seedDefaultData(database: AppDatabase) {
            val profileDao = database.deviceProfileDao()
            val phoneDao = database.virtualPhoneDao()
            val gameDao = database.virtualGameDao()

            // Seed default profiles
            val p1 = DeviceProfile(
                id = 1,
                profileName = "Infinix X6891 (Native Default)",
                manufacturer = "INFINIX",
                brand = "INFINIX",
                model = "Infinix X6891",
                deviceName = "Infinix-X6891",
                androidVersion = "Android 15 (API 35)",
                screenResolution = "1080 x 2400 FHD+",
                displayRefreshRate = 144,
                isDefault = true,
                notes = "Physical device identity profile matching host Infinix X6891 specifications."
            )
            val p2 = DeviceProfile(
                id = 2,
                profileName = "ASUS ROG Phone 8 (144Hz Beast)",
                manufacturer = "ASUS",
                brand = "ASUS",
                model = "ROG Phone 8",
                deviceName = "ASUS_AI2401",
                androidVersion = "Android 15 (API 35)",
                screenResolution = "1080 x 2400 FHD+",
                displayRefreshRate = 144,
                isDefault = false,
                notes = "Unlocks 120-144 FPS in PUBG, COD Mobile and Free Fire MAX."
            )
            val p3 = DeviceProfile(
                id = 3,
                profileName = "Samsung Galaxy S24 Ultra (Flagship)",
                manufacturer = "Samsung",
                brand = "Samsung",
                model = "Galaxy S24 Ultra",
                deviceName = "SM-S928B",
                androidVersion = "Android 15 (API 35)",
                screenResolution = "1440 x 3120 QHD+",
                displayRefreshRate = 120,
                isDefault = false,
                notes = "Optimal for high graphical fidelity with Vulkan ray-tracing compatibility."
            )
            val p4 = DeviceProfile(
                id = 4,
                profileName = "Xiaomi Redmi K80 Ultra (144Hz Speed)",
                manufacturer = "Xiaomi",
                brand = "Xiaomi",
                model = "Redmi K80 Ultra",
                deviceName = "24122RKC7C",
                androidVersion = "Android 15 (API 35)",
                screenResolution = "1220 x 2712 1.5K",
                displayRefreshRate = 144,
                isDefault = false,
                notes = "High-refresh esports profile tailored for BGMI and Free Fire."
            )
            profileDao.insertAll(listOf(p1, p2, p3, p4))

            // Seed default virtual phone
            val phone1 = VirtualPhone(
                id = 1,
                name = "V-Phone 144 Esports Rig",
                deviceProfileId = 2, // ROG Phone 8
                status = "RUNNING",
                allocatedRamMb = 4096,
                allocatedStorageGb = 32,
                cpuCoresLimit = 8,
                targetResolution = "1080x2400",
                targetRefreshRate = 144,
                graphicsMode = "High Performance",
                isolationMode = "Sandboxed Profile Container",
                installedPackagesJson = "[\"com.dts.freefireth\",\"com.dts.freefiremax\",\"com.tencent.ig\",\"com.pubg.imobile\",\"com.activision.callofduty.shooter\"]",
                uptimeSeconds = 360,
                isDefault = true
            )
            phoneDao.insertVirtualPhone(phone1)

            // Seed target games
            val games = listOf(
                VirtualGame(
                    packageName = "com.dts.freefiremax",
                    appName = "Free Fire MAX",
                    versionName = "2.106.1",
                    versionCode = 2019117000,
                    installedInVirtualPhoneId = 1,
                    preferredFps = 144,
                    assignedProfileId = 2,
                    graphicsMode = "High Performance",
                    isAddedToVirtualEnv = true,
                    antiCheatEngine = "Garena HackShield & Integrity Guard",
                    playIntegrityRequirement = "MEETS_DEVICE_INTEGRITY",
                    playServicesRequired = true,
                    nativeAbi = "arm64-v8a",
                    compatibilityStatus = "Fully Supported (Sandboxed Environment)",
                    isOfficialGame = true
                ),
                VirtualGame(
                    packageName = "com.dts.freefireth",
                    appName = "Garena Free Fire",
                    versionName = "1.106.1",
                    versionCode = 2019116000,
                    installedInVirtualPhoneId = 1,
                    preferredFps = 144,
                    assignedProfileId = 1,
                    graphicsMode = "High Performance",
                    isAddedToVirtualEnv = true,
                    antiCheatEngine = "Garena HackShield",
                    playIntegrityRequirement = "MEETS_DEVICE_INTEGRITY",
                    playServicesRequired = true,
                    nativeAbi = "arm64-v8a",
                    compatibilityStatus = "Fully Supported (Sandboxed Environment)",
                    isOfficialGame = true
                ),
                VirtualGame(
                    packageName = "com.tencent.ig",
                    appName = "PUBG Mobile",
                    versionName = "3.5.0",
                    versionCode = 18900,
                    installedInVirtualPhoneId = 1,
                    preferredFps = 144,
                    assignedProfileId = 2,
                    graphicsMode = "High Performance",
                    isAddedToVirtualEnv = true,
                    antiCheatEngine = "Tencent ACE / Anti-Cheat Expert",
                    playIntegrityRequirement = "MEETS_STRONG_INTEGRITY",
                    playServicesRequired = true,
                    nativeAbi = "arm64-v8a",
                    compatibilityStatus = "Supported via Profile Masking (Extreme+ FPS)",
                    isOfficialGame = true
                ),
                VirtualGame(
                    packageName = "com.pubg.imobile",
                    appName = "BGMI (Battlegrounds Mobile)",
                    versionName = "3.5.0",
                    versionCode = 18910,
                    installedInVirtualPhoneId = 1,
                    preferredFps = 144,
                    assignedProfileId = 4,
                    graphicsMode = "High Performance",
                    isAddedToVirtualEnv = true,
                    antiCheatEngine = "Krafton Security & Tencent MTP",
                    playIntegrityRequirement = "MEETS_STRONG_INTEGRITY",
                    playServicesRequired = true,
                    nativeAbi = "arm64-v8a",
                    compatibilityStatus = "Supported (90-144 FPS Profile Match)",
                    isOfficialGame = true
                ),
                VirtualGame(
                    packageName = "com.activision.callofduty.shooter",
                    appName = "Call of Duty: Mobile",
                    versionName = "1.0.48",
                    versionCode = 5400,
                    installedInVirtualPhoneId = 1,
                    preferredFps = 144,
                    assignedProfileId = 2,
                    graphicsMode = "High Performance",
                    isAddedToVirtualEnv = true,
                    antiCheatEngine = "Activision Security & FairPlay",
                    playIntegrityRequirement = "MEETS_DEVICE_INTEGRITY",
                    playServicesRequired = true,
                    nativeAbi = "arm64-v8a",
                    compatibilityStatus = "Supported (Ultra High Frame Rate Mode)",
                    isOfficialGame = true
                )
            )
            gameDao.insertAll(games)
        }
    }
}
