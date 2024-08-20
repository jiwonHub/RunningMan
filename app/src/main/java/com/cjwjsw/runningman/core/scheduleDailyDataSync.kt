package com.cjwjsw.runningman.core

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cjwjsw.runningman.service.worker.DataSyncWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

fun scheduleDailyDataSync(context: Context) {
    // 자정까지의 시간을 계산
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 19)
        set(Calendar.MINUTE, 52)
        set(Calendar.SECOND, 0)
        if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    val initialDelay = target.timeInMillis - now.timeInMillis
    val currentDistance = WalkDataSingleton.distance.value ?: 0.0
    val currentStepCount = WalkDataSingleton.stepCount.value ?: 0
    val currentCalories = WalkDataSingleton.calorie.value ?: 0.0
    val currentTime = WalkDataSingleton.time.value ?: 0L
    val data: Data = workDataOf(
        "distance" to currentDistance,
        "stepCount" to currentStepCount,
        "calories" to currentCalories,
        "time" to currentTime
    )


    // 주기적 워크 요청 설정 (매일 반복)
    val workRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // 처음 시작을 자정으로 맞춤
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DataSyncWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val scheduledTime = sdf.format(target.time)
    Log.d("DataSync", "Work scheduled for: $scheduledTime")
}