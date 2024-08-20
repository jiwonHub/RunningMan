package com.cjwjsw.runningman.service.worker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cjwjsw.runningman.core.FirestoreManager
import com.cjwjsw.runningman.core.LocationTrackerManager
import com.cjwjsw.runningman.core.WalkDataSingleton
import com.cjwjsw.runningman.data.data_source.db.DailyWalk
import com.cjwjsw.runningman.domain.model.WalkModel
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.cjwjsw.runningman.presentation.screen.main.fragment.map.MapViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

const val DISTANCE = "distance"
const val STEPS = "stepCount"
const val CALORIES = "calories"
const val TIME = "time"


@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val walkRepository: WalkRepository,
    private val firestoreManager: FirestoreManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = getCurrentDate()
            val distance = inputData.getDouble(DISTANCE, 0.0)
            val stepCount = inputData.getInt(STEPS, 0)
            val calories = inputData.getDouble(CALORIES, 0.0)
            val time = inputData.getLong(TIME, 0L)

            // Room에 데이터 저장
            val dailyWalk = DailyWalk(
                date = today,
                distance = distance,
                stepCount = stepCount,
                calories = calories,
                time = time
            )
            walkRepository.insertWalk(dailyWalk)

            // Firestore에 저장
            firestoreManager.saveWalk(WalkModel(
                date = today,
                distance = distance,
                stepCount = stepCount,
                calories = calories,
                time = time
            ))

            //저장 후 데이터 초기화
            WalkDataSingleton.resetDistance()
            WalkDataSingleton.resetStepCount()
            WalkDataSingleton.resetCalorie()
            WalkDataSingleton.resetTime()

            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing data", e)
            Result.failure()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)  // 현재 날짜에서 하루를 뺍니다
        }
        return sdf.format(calendar.time)
    }
}