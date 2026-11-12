package com.example.vphone.data.repository

import com.example.vphone.data.db.VirtualPhoneDao
import com.example.vphone.data.model.VirtualPhone
import kotlinx.coroutines.flow.Flow

class VirtualPhoneRepository(private val dao: VirtualPhoneDao) {
    val allVirtualPhones: Flow<List<VirtualPhone>> = dao.getAllVirtualPhones()
    val runningPhone: Flow<VirtualPhone?> = dao.getRunningVirtualPhone()

    suspend fun getVirtualPhoneById(id: Long): VirtualPhone? = dao.getVirtualPhoneById(id)

    suspend fun saveVirtualPhone(phone: VirtualPhone): Long {
        return if (phone.id == 0L) {
            dao.insertVirtualPhone(phone)
        } else {
            dao.updateVirtualPhone(phone)
            phone.id
        }
    }

    suspend fun deleteVirtualPhone(phone: VirtualPhone) = dao.deleteVirtualPhone(phone)

    suspend fun updateStatus(id: Long, status: String) = dao.updateStatus(id, status)

    suspend fun incrementRunningUptime(seconds: Long) = dao.incrementRunningUptime(seconds)

    suspend fun setDefaultPhone(id: Long) {
        dao.clearDefault()
        dao.setDefault(id)
    }

    suspend fun duplicateVirtualPhone(phone: VirtualPhone): Long {
        val duplicated = phone.copy(
            id = 0,
            name = "${phone.name} (Clone)",
            status = "STOPPED",
            uptimeSeconds = 0,
            createdAt = System.currentTimeMillis(),
            isDefault = false
        )
        return dao.insertVirtualPhone(duplicated)
    }
}
