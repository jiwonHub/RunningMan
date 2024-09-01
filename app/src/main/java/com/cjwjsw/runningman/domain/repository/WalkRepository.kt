package com.cjwjsw.runningman.domain.repository

import com.cjwjsw.runningman.data.data_source.db.DailyWalk

interface WalkRepository {

    suspend fun getWalkByDate(date: String): DailyWalk

    suspend fun insertWalk(walk: DailyWalk)

    suspend fun getAllWalks(): List<DailyWalk>

    suspend fun getWalksBetweenDates(startDate: String, endDate: String): List<DailyWalk>

    suspend fun getWalkCount(): Int

    suspend fun deleteAllWalks()
}