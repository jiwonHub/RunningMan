package com.cjwjsw.runningman.data.data_source.db.water

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WaterDao {

    @Query("SELECT * FROM water")
    suspend fun getAllWaters(): List<WaterEntity>

    @Query("SELECT * FROM water WHERE date = :date LIMIT 1")
    suspend fun getWaterByDate(date: String): WaterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(walk: WaterEntity)

    @Query("SELECT COUNT(*) FROM water")
    suspend fun getWatersCount(): Int

    @Query("SELECT * FROM water WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getWatersBetweenDates(startDate: String, endDate: String): List<WaterEntity>

    @Query("DELETE FROM water")
    suspend fun deleteAllWaters()

}