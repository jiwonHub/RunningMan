package com.cjwjsw.runningman.domain.repository

import com.cjwjsw.runningman.data.data_source.db.walk.DailyWalkEntity

interface WalkRepository {

    suspend fun getWalkByDate(date: String): DailyWalkEntity

    suspend fun insertWalk(walk: DailyWalkEntity)

    suspend fun getAllWalks(): List<DailyWalkEntity>

    suspend fun getWalksBetweenDates(startDate: String, endDate: String): List<DailyWalkEntity>

    suspend fun getWalkCount(): Int

    suspend fun deleteAllWalks()
}