package com.example.data.dao

import androidx.room.*
import com.example.data.entity.LevelProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllProgress(): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    fun getProgressById(levelId: Int): Flow<LevelProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LevelProgress)

    @Update
    suspend fun updateProgress(progress: LevelProgress)

    @Query("UPDATE level_progress SET unlocked = 1 WHERE levelId = :levelId")
    suspend fun unlockLevel(levelId: Int)

    @Query("DELETE FROM level_progress")
    suspend fun clearAll()
}
