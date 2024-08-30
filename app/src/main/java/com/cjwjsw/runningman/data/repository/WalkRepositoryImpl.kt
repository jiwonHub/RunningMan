package com.cjwjsw.runningman.data.repository

import android.util.Log
import com.cjwjsw.runningman.data.data_source.db.DailyWalk
import com.cjwjsw.runningman.data.data_source.db.WalkDao
import com.cjwjsw.runningman.domain.model.WalkModel
import com.cjwjsw.runningman.domain.repository.WalkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalkRepositoryImpl @Inject constructor(
    private val walkDao: WalkDao
) : WalkRepository {
    override suspend fun getWalkByDate(date: String): DailyWalk = withContext(Dispatchers.IO) {
        walkDao.getWalkByDate(date) ?: DailyWalk(date, 0.0, 0, 0.0, 0L)
    }

    override suspend fun insertWalk(walk: DailyWalk) = withContext(Dispatchers.IO) {
        walkDao.insertWalk(walk)
    }

    override suspend fun getAllWalks(): List<DailyWalk> = withContext(Dispatchers.IO) {
        walkDao.getAllWalks()
    }

    override suspend fun getWalksBetweenDates(startDate: String, endDate: String): List<DailyWalk> = withContext(Dispatchers.IO) {
        walkDao.getWalksBetweenDates(startDate, endDate)
    }

    override suspend fun getWalkCount(): Int = withContext(Dispatchers.IO) {
        walkDao.getWalkCount()
    }

    override suspend fun deleteAllWalks()  = withContext(Dispatchers.IO){
        walkDao.deleteAllWalks()
    }

}