package com.cjwjsw.runningman.data.data_source.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DailyWalk::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkDao(): WalkDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 새 컬럼을 기존 테이블에 추가합니다.
        db.execSQL("ALTER TABLE daily_walk ADD COLUMN stepCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE daily_walk ADD COLUMN calories REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE daily_walk ADD COLUMN time INTEGER NOT NULL DEFAULT 0")
    }
}
