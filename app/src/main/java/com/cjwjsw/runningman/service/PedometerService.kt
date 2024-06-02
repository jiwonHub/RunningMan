package com.cjwjsw.runningman.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.presentation.screen.main.MainActivity
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

class PedometerService : Service(), SensorEventListener {
    private var mockDataJob: Job? = null

    // 센서 매니저
    private lateinit var sensorManager: SensorManager
    // 걸음 센서
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0 // 걸음 수
    private var caloriesBurned = 0.0 // 칼로리 소모
    private var initialStepCount = 0 // 이전에 걸었던 수
    private var distanceWalked = 0.0 // 거리 계산
    private var elapsedTime = 0L // 시간 계산
    private var lastCheckedDay = -1
    private lateinit var sharedPreferences: SharedPreferences

    // 알림 관리자 및 빌더
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId = 1
    private val channelId = "PedometerServiceChannel"

    // 라이브 데이터
    companion object {
        val stepCountLiveData = MutableLiveData<Int>()
        val caloriesBurnedLiveData = MutableLiveData<Double>()
        val totalStepCountLiveData = MutableLiveData<Int>()
        val distanceWalkedLiveData = MutableLiveData<Double>()
        val elapsedTimeLiveData = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("PedometerPreferences", Context.MODE_PRIVATE)
        // 센서 매니저 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 걸음 센서 초기화
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor != null) {
            // 걸음 센서 리스너 등록
            val registered = sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (!registered) {
                stopSelf() // 리스너 등록 실패 시 서비스 종료
            }
        } else {
            stopSelf() // 센서가 없을 경우 서비스 종료
        }

        // 알림 관리자 초기화
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel() // 알림 채널 생성

        notificationBuilder = createNotificationBuilder()
        val notification = notificationBuilder.build()
        startForeground(notificationId, notification)

        initialStepCount = sharedPreferences.getInt("initialStepCount", 0)
        lastCheckedDay = sharedPreferences.getInt("lastCheckedDay", -1)

        // 경과 시간 카운터 시작
        startElapsedTimeCounter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationBuilder.build()
        startForeground(notificationId, notification)
        // 테스트 목적으로 모의 데이터 생성
        // generateMockData()
        return START_STICKY // 서비스가 종료된 경우 자동 재시작
    }

    private fun startElapsedTimeCounter() {
        GlobalScope.launch {
            while (true) {
                delay(1000) // 1초마다
                elapsedTime++  // 경과 시간 1초 증가
                elapsedTimeLiveData.postValue(elapsedTime) // 라이브 데이터 업데이트
            }
        }
    }

//    private fun generateMockData(): Job {
//        GlobalScope.launch {
//            while (true) {
//                delay(1000)
//                stepCount++
//                caloriesBurned = stepCount * 0.04 // 칼로리 계산
//                distanceWalked = stepCount * 0.762 / 1000 // 거리 계산 (킬로미터 단위)
//                stepCountLiveData.postValue(stepCount) // 걸음 수 라이브 데이터 업데이트
//                caloriesBurnedLiveData.postValue(caloriesBurned) // 소모 칼로리 라이브 데이터 업데이트
//                totalStepCountLiveData.postValue(initialStepCount + stepCount) // 총 걸음 수 라이브 데이터 업데이트
//                distanceWalkedLiveData.postValue(BigDecimal(distanceWalked).setScale(2, RoundingMode.HALF_UP).toDouble()) // 거리 라이브 데이터 업데이트 (소수점 2자리)
//            }
//        }
//

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("걸음 측정중!")
            .setContentText("열심히 걸으면 복이 찾아옵니다!")
            .setSmallIcon(R.drawable.running)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pedometer Service", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            val calendar = Calendar.getInstance()
            val currentDay = calendar.get(Calendar.DAY_OF_YEAR)

            // 자정에 걸음 수를 초기화하는 로직
            if (currentDay != lastCheckedDay) {
                initialStepCount = totalSteps
                lastCheckedDay = currentDay
                sharedPreferences.edit().putInt("initialStepCount", initialStepCount).apply()
                sharedPreferences.edit().putInt("lastCheckedDay", lastCheckedDay).apply()
            }

            stepCount = totalSteps - initialStepCount
            caloriesBurned = stepCount * 0.04
            distanceWalked = stepCount * 0.762 / 1000 // km

            stepCountLiveData.postValue(stepCount)
            caloriesBurnedLiveData.postValue(caloriesBurned)
            totalStepCountLiveData.postValue(totalSteps)
            distanceWalkedLiveData.postValue(BigDecimal(distanceWalked).setScale(2, RoundingMode.HALF_UP).toDouble())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("PedometerService", "Sensor accuracy changed: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

        sharedPreferences.edit().putInt("initialStepCount", initialStepCount).apply()
        sharedPreferences.edit().putInt("lastCheckedDay", lastCheckedDay).apply()

//        mockDataJob?.cancel()
    }
}
