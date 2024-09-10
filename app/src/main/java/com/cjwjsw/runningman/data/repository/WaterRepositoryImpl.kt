package com.cjwjsw.runningman.data.repository

import com.cjwjsw.runningman.data.data_source.db.water.WaterDao
import com.cjwjsw.runningman.data.data_source.db.water.WaterEntity
import com.cjwjsw.runningman.domain.repository.WaterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaterRepositoryImpl @Inject constructor(
    private val waterDao: WaterDao
) : WaterRepository {
    override suspend fun getWaterByDate(date: String): WaterEntity = withContext(Dispatchers.IO) {
        waterDao.getWaterByDate(date) ?: WaterEntity(date, 0)
    }

    override suspend fun insertWater(walk: WaterEntity) = withContext(Dispatchers.IO) {
        waterDao.insertWater(walk)
    }

    override suspend fun getAllWaters(): List<WaterEntity> = withContext(Dispatchers.IO) {
        waterDao.getAllWaters()
    }

    override suspend fun getWatersBetweenDates(startDate: String, endDate: String): List<WaterEntity> = withContext(Dispatchers.IO) {
        waterDao.getWatersBetweenDates(startDate, endDate)
    }

    override suspend fun getWaterCount(): Int = withContext(Dispatchers.IO) {
        waterDao.getWatersCount()
    }

    override suspend fun deleteAllWaters()  = withContext(Dispatchers.IO){
        waterDao.deleteAllWaters()
    }

}