package com.example.vphone.data.repository

import com.example.vphone.data.db.DeviceProfileDao
import com.example.vphone.data.model.DeviceProfile
import kotlinx.coroutines.flow.Flow

class DeviceProfileRepository(private val dao: DeviceProfileDao) {
    val allProfiles: Flow<List<DeviceProfile>> = dao.getAllProfiles()

    suspend fun getProfileById(id: Long): DeviceProfile? = dao.getProfileById(id)

    suspend fun getDefaultProfile(): DeviceProfile? = dao.getDefaultProfile()

    suspend fun saveProfile(profile: DeviceProfile): Long {
        return if (profile.id == 0L) {
            dao.insertProfile(profile)
        } else {
            dao.updateProfile(profile)
            profile.id
        }
    }

    suspend fun deleteProfile(profile: DeviceProfile) = dao.deleteProfile(profile)

    suspend fun setDefaultProfile(id: Long) {
        dao.clearDefaultProfiles()
        dao.setDefaultProfile(id)
    }

    suspend fun duplicateProfile(profile: DeviceProfile): Long {
        val duplicated = profile.copy(
            id = 0,
            profileName = "${profile.profileName} (Copy)",
            isDefault = false
        )
        return dao.insertProfile(duplicated)
    }
}
