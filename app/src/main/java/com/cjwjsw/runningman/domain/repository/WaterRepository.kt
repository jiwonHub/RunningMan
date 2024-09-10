package com.cjwjsw.runningman.domain.repository

import com.cjwjsw.runningman.data.data_source.db.water.WaterEntity

interface WaterRepository {

    suspend fun getWaterByDate(date: String): WaterEntity

    suspend fun insertWater(walk: WaterEntity)

    suspend fun getAllWaters(): List<WaterEntity>

    suspend fun getWatersBetweenDates(startDate: String, endDate: String): List<WaterEntity>

    suspend fun getWaterCount(): Int

    suspend fun deleteAllWaters()
}