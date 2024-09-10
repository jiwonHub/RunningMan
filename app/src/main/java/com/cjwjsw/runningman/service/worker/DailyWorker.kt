package com.cjwjsw.runningman.service.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cjwjsw.runningman.core.DataSingleton
import com.cjwjsw.runningman.data.data_source.db.water.WaterEntity
import com.cjwjsw.runningman.domain.repository.WaterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val waterRepository: WaterRepository
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            // 데이터 초기화
            DataSingleton.resetDistance()
            DataSingleton.resetStepCount()
            DataSingleton.resetCalorie()
            DataSingleton.resetTime()

            // 어제 날짜 가져오기
            val previousDate = java.time.LocalDate.now().minusDays(1).toString() // 날짜 형식: "yyyy-MM-dd"

            // WaterEntity 객체 생성
            val water = WaterEntity(
                date = previousDate,
                water = DataSingleton.drinkingWater.value?.toInt() ?: 0
            )
            waterRepository.insertWater(water)
            DataSingleton.resetDrinkingWater()

            Log.d("DataResetWorker", "Data reset at midnight.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataResetWorker", "Error resetting data", e)
            Result.failure()
        }
    }
}