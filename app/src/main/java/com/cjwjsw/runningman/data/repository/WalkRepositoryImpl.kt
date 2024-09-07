package com.cjwjsw.runningman.data.repository

import com.cjwjsw.runningman.data.data_source.db.walk.DailyWalkEntity
import com.cjwjsw.runningman.data.data_source.db.walk.WalkDao
import com.cjwjsw.runningman.domain.repository.WalkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalkRepositoryImpl @Inject constructor(
    private val walkDao: WalkDao
) : WalkRepository {
    override suspend fun getWalkByDate(date: String): DailyWalkEntity = withContext(Dispatchers.IO) {
        walkDao.getWalkByDate(date) ?: DailyWalkEntity(date, 0.0, 0, 0.0, 0L)
    }

    override suspend fun insertWalk(walk: DailyWalkEntity) = withContext(Dispatchers.IO) {
        walkDao.insertWalk(walk)
    }

    override suspend fun getAllWalks(): List<DailyWalkEntity> = withContext(Dispatchers.IO) {
        walkDao.getAllWalks()
    }

    override suspend fun getWalksBetweenDates(startDate: String, endDate: String): List<DailyWalkEntity> = withContext(Dispatchers.IO) {
        walkDao.getWalksBetweenDates(startDate, endDate)
    }

    override suspend fun getWalkCount(): Int = withContext(Dispatchers.IO) {
        walkDao.getWalkCount()
    }

    override suspend fun deleteAllWalks()  = withContext(Dispatchers.IO){
        walkDao.deleteAllWalks()
    }

}