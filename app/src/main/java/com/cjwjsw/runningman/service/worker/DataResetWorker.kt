package com.cjwjsw.runningman.service.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cjwjsw.runningman.core.WalkDataSingleton
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 데이터 초기화
            WalkDataSingleton.resetDistance()
            WalkDataSingleton.resetStepCount()
            WalkDataSingleton.resetCalorie()
            WalkDataSingleton.resetTime()

            Log.d("DataResetWorker", "Data reset at midnight.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataResetWorker", "Error resetting data", e)
            Result.failure()
        }
    }
}