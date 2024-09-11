package com.cjwjsw.runningman

import android.app.Application

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cjwjsw.runningman.core.Settings
import com.cjwjsw.runningman.core.scheduleHourlyDataSync
import com.cjwjsw.runningman.core.scheduleMidnightDataReset
import com.cjwjsw.runningman.service.LocationUpdateService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RunningManApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // 매일 자정에 Room과 Firestore에 데이터를 저장하는 작업을 예약
        scheduleHourlyDataSync(this)
        scheduleMidnightDataReset(this)
        startLocationService()
        Settings.init(this)
    }

    private fun startLocationService() {
        val intent = LocationUpdateService.newIntent(this)
        startService(intent)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(hiltWorkerFactory)
        .build()
}
