package com.cjwjsw.runningman

import android.app.Application

import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cjwjsw.runningman.core.scheduleDailyDataSync
import com.cjwjsw.runningman.service.LocationUpdateService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.math.log

@HiltAndroidApp
class RunningManApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // 매일 자정에 Room과 Firestore에 데이터를 저장하는 작업을 예약
        scheduleDailyDataSync(this)
        startLocationService()
    }

    private fun startLocationService() {
        val intent = LocationUpdateService.newIntent(this)
        startService(intent)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(hiltWorkerFactory)
        .build()
}
