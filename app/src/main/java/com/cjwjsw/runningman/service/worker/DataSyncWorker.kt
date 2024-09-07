package com.cjwjsw.runningman.service.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cjwjsw.runningman.core.PreviousWalkData
import com.cjwjsw.runningman.core.WalkDataSingleton
import com.cjwjsw.runningman.data.data_source.db.walk.DailyWalkEntity
import com.cjwjsw.runningman.domain.repository.WalkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val walkRepository: WalkRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DataSyncWorker", "Work started at: ${System.currentTimeMillis()}")

            // 현재 상태 가져오기
            val currentDate = getCurrentDate()
            val currentDistance = WalkDataSingleton.distance.value ?: 0.0
            val currentStepCount = WalkDataSingleton.stepCount.value ?: 0
            val currentCalories = WalkDataSingleton.calorie.value ?: 0.0
            val currentTime = WalkDataSingleton.time.value ?: 0L

            // 이전 상태와의 변화량 계산
            val deltaDistance = currentDistance - PreviousWalkData.previousDistance
            val deltaStepCount = currentStepCount - PreviousWalkData.previousStepCount
            val deltaCalories = currentCalories - PreviousWalkData.previousCalories
            val deltaTime = currentTime - PreviousWalkData.previousTime

            // 변화량이 유효한 경우에만 저장
            if (deltaStepCount > 0 || deltaDistance > 0 || deltaCalories > 0 || deltaTime > 0) {
                val hourlyWalk = DailyWalkEntity(
                    date = currentDate,
                    distance = deltaDistance,
                    stepCount = deltaStepCount,
                    calories = deltaCalories,
                    time = deltaTime
                )
                walkRepository.insertWalk(hourlyWalk)
                Log.d("DataSyncWorker", "Saved to Room: $hourlyWalk")
            } else {
                Log.d("DataSyncWorker", "No new data to save.")
            }

            // 현재 상태를 이전 상태로 업데이트
            PreviousWalkData.updatePreviousData(currentDistance, currentStepCount, currentCalories, currentTime)

            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing data", e)
            Result.failure()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd-HH", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return sdf.format(calendar.time)
    }
}