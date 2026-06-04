package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val levelId: Int,
    val unlocked: Boolean,
    val completed: Boolean,
    val starsFound: Int,
    val bestTimeSeconds: Int
)
