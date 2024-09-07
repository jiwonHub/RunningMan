package com.cjwjsw.runningman.data.data_source.db.walk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_walk")
data class DailyWalkEntity(
    @PrimaryKey val date: String,
    val distance: Double,
    val stepCount: Int,
    val calories: Double,
    val time: Long
)