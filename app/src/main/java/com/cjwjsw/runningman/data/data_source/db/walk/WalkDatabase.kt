package com.cjwjsw.runningman.data.data_source.db.walk

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyWalkEntity::class], version = 1)
abstract class WalkAppDatabase : RoomDatabase() {
    abstract fun walkDao(): WalkDao
}
