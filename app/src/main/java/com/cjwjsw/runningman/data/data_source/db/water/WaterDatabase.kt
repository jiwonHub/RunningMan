package com.cjwjsw.runningman.data.data_source.db.water

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WaterEntity::class], version = 1)
abstract class WaterAppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
}
