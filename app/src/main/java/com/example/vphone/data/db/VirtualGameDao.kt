package com.example.vphone.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vphone.data.model.VirtualGame
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualGameDao {
    @Query("SELECT * FROM virtual_games ORDER BY appName ASC")
    fun getAllGames(): Flow<List<VirtualGame>>

    @Query("SELECT * FROM virtual_games WHERE isAddedToVirtualEnv = 1 ORDER BY appName ASC")
    fun getVirtualEnvironmentGames(): Flow<List<VirtualGame>>

    @Query("SELECT * FROM virtual_games WHERE packageName = :packageName LIMIT 1")
    suspend fun getGameByPackage(packageName: String): VirtualGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: VirtualGame)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<VirtualGame>)

    @Update
    suspend fun updateGame(game: VirtualGame)

    @Delete
    suspend fun deleteGame(game: VirtualGame)

    @Query("UPDATE virtual_games SET preferredFps = :fps WHERE packageName = :packageName")
    suspend fun updateFps(packageName: String, fps: Int)

    @Query("UPDATE virtual_games SET assignedProfileId = :profileId WHERE packageName = :packageName")
    suspend fun updateAssignedProfile(packageName: String, profileId: Long)
}
