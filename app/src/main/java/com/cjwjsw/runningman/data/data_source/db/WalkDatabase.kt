package com.cjwjsw.runningman.data.data_source.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyWalk::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkDao(): WalkDao
}