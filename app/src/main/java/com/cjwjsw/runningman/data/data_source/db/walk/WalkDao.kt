package com.cjwjsw.runningman.data.data_source.db.walk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WalkDao {

    @Query("SELECT * FROM daily_walk")
    suspend fun getAllWalks(): List<DailyWalkEntity>

    @Query("SELECT * FROM daily_walk WHERE date = :date LIMIT 1")
    suspend fun getWalkByDate(date: String): DailyWalkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalk(walk: DailyWalkEntity)

    @Query("SELECT COUNT(*) FROM daily_walk")
    suspend fun getWalkCount(): Int

    @Query("SELECT * FROM daily_walk WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getWalksBetweenDates(startDate: String, endDate: String): List<DailyWalkEntity>

    @Query("DELETE FROM daily_walk")
    suspend fun deleteAllWalks()
}