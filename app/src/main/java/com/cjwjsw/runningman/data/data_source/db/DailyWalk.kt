package com.cjwjsw.runningman.data.data_source.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_walk")
data class DailyWalk(
    @PrimaryKey val date: String,
    val distance: Double
)