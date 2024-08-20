package com.cjwjsw.runningman.data.data_source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WalkDao {

    @Query("SELECT * FROM daily_walk")
    suspend fun getAllWalks(): List<DailyWalk>

    @Query("SELECT * FROM daily_walk WHERE date = :date LIMIT 1")
    suspend fun getWalkByDate(date: String): DailyWalk?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalk(walk: DailyWalk)

    @Query("SELECT COUNT(*) FROM daily_walk")
    suspend fun getWalkCount(): Int
}