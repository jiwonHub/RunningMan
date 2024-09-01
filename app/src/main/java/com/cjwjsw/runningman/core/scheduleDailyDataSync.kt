package com.cjwjsw.runningman.core

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cjwjsw.runningman.service.worker.DataResetWorker
import com.cjwjsw.runningman.service.worker.DataSyncWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

fun scheduleHourlyDataSync(context: Context) {
    // 1시간마다 데이터 동기화 작업 설정
    val workRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(1, TimeUnit.HOURS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "HourlyDataSyncWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )

    Log.d("DataSync", "Hourly data sync scheduled every 1 hour.")
}

fun scheduleMidnightDataReset(context: Context) {
    // 자정에 데이터를 초기화하는 작업 스케줄링
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    val initialDelay = target.timeInMillis - now.timeInMillis

    // 주기적 워크 요청 설정 (매일 자정 반복)
    val workRequest = PeriodicWorkRequestBuilder<DataResetWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "MidnightDataResetWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val scheduledTime = sdf.format(target.time)
    Log.d("DataReset", "Data reset scheduled for midnight: $scheduledTime")
}