package com.example.vphone.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vphone.data.model.DeviceProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceProfileDao {
    @Query("SELECT * FROM device_profiles ORDER BY isDefault DESC, profileName ASC")
    fun getAllProfiles(): Flow<List<DeviceProfile>>

    @Query("SELECT * FROM device_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): DeviceProfile?

    @Query("SELECT * FROM device_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfile(): DeviceProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: DeviceProfile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<DeviceProfile>)

    @Update
    suspend fun updateProfile(profile: DeviceProfile)

    @Delete
    suspend fun deleteProfile(profile: DeviceProfile)

    @Query("UPDATE device_profiles SET isDefault = 0")
    suspend fun clearDefaultProfiles()

    @Query("UPDATE device_profiles SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultProfile(id: Long)
}
