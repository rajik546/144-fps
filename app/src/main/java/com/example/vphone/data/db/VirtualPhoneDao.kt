package com.example.vphone.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vphone.data.model.VirtualPhone
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualPhoneDao {
    @Query("SELECT * FROM virtual_phones ORDER BY isDefault DESC, createdAt DESC")
    fun getAllVirtualPhones(): Flow<List<VirtualPhone>>

    @Query("SELECT * FROM virtual_phones WHERE id = :id LIMIT 1")
    suspend fun getVirtualPhoneById(id: Long): VirtualPhone?

    @Query("SELECT * FROM virtual_phones WHERE status = 'RUNNING' LIMIT 1")
    fun getRunningVirtualPhone(): Flow<VirtualPhone?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVirtualPhone(phone: VirtualPhone): Long

    @Update
    suspend fun updateVirtualPhone(phone: VirtualPhone)

    @Delete
    suspend fun deleteVirtualPhone(phone: VirtualPhone)

    @Query("UPDATE virtual_phones SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE virtual_phones SET uptimeSeconds = uptimeSeconds + :seconds WHERE status = 'RUNNING'")
    suspend fun incrementRunningUptime(seconds: Long)

    @Query("UPDATE virtual_phones SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE virtual_phones SET isDefault = 1 WHERE id = :id")
    suspend fun setDefault(id: Long)
}
