package com.example.vphone.data.repository

import com.example.vphone.data.db.VirtualGameDao
import com.example.vphone.data.model.VirtualGame
import kotlinx.coroutines.flow.Flow

class VirtualGameRepository(private val dao: VirtualGameDao) {
    val allGames: Flow<List<VirtualGame>> = dao.getAllGames()
    val virtualEnvGames: Flow<List<VirtualGame>> = dao.getVirtualEnvironmentGames()

    suspend fun getGameByPackage(packageName: String): VirtualGame? = dao.getGameByPackage(packageName)

    suspend fun saveGame(game: VirtualGame) = dao.insertGame(game)

    suspend fun insertAll(games: List<VirtualGame>) = dao.insertAll(games)

    suspend fun deleteGame(game: VirtualGame) = dao.deleteGame(game)

    suspend fun updateFps(packageName: String, fps: Int) = dao.updateFps(packageName, fps)

    suspend fun updateAssignedProfile(packageName: String, profileId: Long) =
        dao.updateAssignedProfile(packageName, profileId)
}
