package com.cjwjsw.runningman.data.repository

import android.util.Log
import com.cjwjsw.runningman.data.data_source.db.DailyWalk
import com.cjwjsw.runningman.data.data_source.db.WalkDao
import com.cjwjsw.runningman.domain.model.WalkModel
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.cjwjsw.runningman.core.FirestoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WalkRepositoryImpl @Inject constructor(
    private val walkDao: WalkDao,
    private val firestoreManager: FirestoreManager
): WalkRepository {
    override suspend fun getWalkByDate(date: String): DailyWalk = withContext(Dispatchers.IO) {
        val roomWalk = walkDao.getWalkByDate(date) ?: DailyWalk(date, 0.0)
        // Firestore에서 데이터를 가져옴 (필요한 경우)
        val firestoreWalk = firestoreManager.getWalkByDate(date)

        // Firestore 데이터가 있으면 그 데이터를 사용, 아니면 Room 데이터를 반환
        firestoreWalk?.let {
            return@withContext DailyWalk(it.date, it.distance)
        } ?: roomWalk
    }

    override suspend fun insertWalk(walk: DailyWalk) = withContext(Dispatchers.IO){
        walkDao.insertWalk(walk)
    }

    override suspend fun insertFireStoreWalk(walk: DailyWalk) = withContext(Dispatchers.IO){
        firestoreManager.saveWalk(WalkModel(walk.date, walk.distance))
    }

    override suspend fun getAllWalks(): List<DailyWalk> = withContext(Dispatchers.IO) {
        walkDao.getAllWalks()
    }

    override suspend fun getWalkCount(): Int = withContext(Dispatchers.IO) {
        walkDao.getWalkCount()
    }

}