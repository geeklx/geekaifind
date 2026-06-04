package com.example.data.repository

import com.example.data.dao.LevelProgressDao
import com.example.data.entity.LevelProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GameRepository(private val dao: LevelProgressDao) {

    val allProgress: Flow<List<LevelProgress>> = dao.getAllProgress()

    fun getProgressForLevel(levelId: Int): Flow<LevelProgress?> {
        return dao.getProgressById(levelId)
    }

    suspend fun initializeDatabaseIfEmpty() {
        val currentProgressList = dao.getAllProgress().first()
        if (currentProgressList.isEmpty()) {
            // Populate progress values for levels 1 to 20
            for (levelId in 1..20) {
                dao.insertProgress(
                    LevelProgress(
                        levelId = levelId,
                        unlocked = levelId == 1, // Level 1 is unlocked initially
                        completed = false,
                        starsFound = 0,
                        bestTimeSeconds = -1
                    )
                )
            }
        }
    }

    suspend fun completeLevel(levelId: Int, stars: Int, timeSeconds: Int) {
        val current = dao.getProgressById(levelId).first()
        val bestTime = if (current != null && current.bestTimeSeconds > 0) {
            minOf(current.bestTimeSeconds, timeSeconds)
        } else {
            timeSeconds
        }
        val bestStars = if (current != null) maxOf(current.starsFound, stars) else stars

        dao.insertProgress(
            LevelProgress(
                levelId = levelId,
                unlocked = true,
                completed = true,
                starsFound = bestStars,
                bestTimeSeconds = bestTime
            )
        )

        // Unlock next level (up to 20)
        if (levelId < 20) {
            val next = dao.getProgressById(levelId + 1).first()
            dao.insertProgress(
                LevelProgress(
                    levelId = levelId + 1,
                    unlocked = true,
                    completed = next?.completed ?: false,
                    starsFound = next?.starsFound ?: 0,
                    bestTimeSeconds = next?.bestTimeSeconds ?: -1
                )
            )
        }
    }

    suspend fun resetAllProgress() {
        dao.clearAll()
        for (levelId in 1..20) {
            dao.insertProgress(
                LevelProgress(
                    levelId = levelId,
                    unlocked = levelId == 1,
                    completed = false,
                    starsFound = 0,
                    bestTimeSeconds = -1
                )
            )
        }
    }
}
