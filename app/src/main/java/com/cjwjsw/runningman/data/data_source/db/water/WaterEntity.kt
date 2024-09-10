package com.cjwjsw.runningman.data.data_source.db.water

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water")
data class WaterEntity(
    @PrimaryKey val date: String,
    val water: Int
)