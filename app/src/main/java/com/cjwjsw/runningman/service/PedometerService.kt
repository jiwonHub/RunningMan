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
import com.cjwjsw.runningman.core.DataSingleton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class PedometerService : Service(), SensorEventListener {
    // 센서 매니저
    private lateinit var sensorManager: SensorManager
    // 걸음 센서
    private var stepCounterSensor: Sensor? = null
    private var initialStepCount = 0 // 이전에 걸었던 수
    private var elapsedTime = 0L // 시간 계산
    private var lastCheckedDay = -1
    private lateinit var sharedPreferences: SharedPreferences

    // 알림 관리자 및 빌더
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId = 1
    private val channelId = "PedometerServiceChannel"

    private var isTimerRunning = false // 타이머가 실행 중인지 확인하는 플래그


    override fun onCreate() {
        super.onCreate()
        Log.d("PedometerService", "Service created")
        sharedPreferences = getSharedPreferences("PedometerPreferences", Context.MODE_PRIVATE)
        initializeSensorManager()
        initializeNotification()
        startElapsedTimeCounter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PedometerService", "Service started")
        startForeground(notificationId, notificationBuilder.build())
        return START_STICKY
    }

    private fun initializeSensorManager() {
        Log.d("PedometerService", "Initializing Sensor Manager")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor == null) {
            Log.e("PedometerService", "Step counter sensor not available!")
            stopSelf()
            return
        }
        if (!sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)) {
            Log.e("PedometerService", "Failed to register sensor listener!")
            stopSelf()
        } else {
            Log.d("PedometerService", "Sensor listener registered successfully.")
        }
        initialStepCount = sharedPreferences.getInt("initialStepCount", 0)
        lastCheckedDay = sharedPreferences.getInt("lastCheckedDay", -1)
    }

    private fun initializeNotification() {
        Log.d("PedometerService", "Initializing Notification")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        notificationBuilder = createNotificationBuilder()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pedometer Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pedometer tracking your steps."
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        Log.d("PedometerService", "Creating Notification Builder")
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

    private fun startElapsedTimeCounter() {
        isTimerRunning = true
        GlobalScope.launch {
            while (isTimerRunning) {
                delay(1000)
                elapsedTime++
                DataSingleton.updateTime(elapsedTime)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                handleStepCounter(it)
            }
        }
    }

    private fun handleStepCounter(event: SensorEvent) {
        val totalSteps = event.values[0].toInt()
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)

        Log.d("PedometerService", "Total Steps: $totalSteps, Initial Step Count: $initialStepCount")

        if (currentDay != lastCheckedDay) {
            initialStepCount = totalSteps
            lastCheckedDay = currentDay
            sharedPreferences.edit().apply {
                putInt("initialStepCount", initialStepCount)
                putInt("lastCheckedDay", lastCheckedDay)
                apply()
            }
        }

        // totalSteps 값이 초기화되었거나 예상보다 작을 경우, 초기 걸음 수 재설정
        if (totalSteps < initialStepCount || totalSteps < 1000) {
            initialStepCount = totalSteps
            sharedPreferences.edit().putInt("initialStepCount", initialStepCount).apply()
        }

        val stepCount = totalSteps - initialStepCount
        Log.d("PedometerService", "Calculated Step Count: $stepCount")

        DataSingleton.updateStepCount(stepCount)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("PedometerService", "Sensor accuracy changed: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        isTimerRunning = false
        sensorManager.unregisterListener(this)
        sharedPreferences.edit().apply {
            putInt("initialStepCount", initialStepCount)
            putInt("lastCheckedDay", lastCheckedDay)
            apply()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
