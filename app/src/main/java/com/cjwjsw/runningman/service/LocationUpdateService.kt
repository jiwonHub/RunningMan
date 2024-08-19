package com.cjwjsw.runningman.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.LocationTrackerManager
import com.cjwjsw.runningman.presentation.screen.main.fragment.map.MapViewModel
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdateService : Service() {

    @Inject
    lateinit var locationTrackerManager: LocationTrackerManager
    private val notificationId = 1
    private val channelId = "LocationServiceChannel"

    override fun onCreate() {
        super.onCreate()
        if (!hasLocationPermissions()) {
            showPermissionErrorAndStop()
            return
        }
        createNotificationChannel()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!hasLocationPermissions()) {
            stopSelf()
            return START_NOT_STICKY
        }

        startLocationUpdates()

        return START_STICKY
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                        ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.FOREGROUND_SERVICE_LOCATION
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(notificationId, notification)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("위치 추적중")
            .setContentText("현재 위치를 추적하고 있습니다.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Tracking Service",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Location tracking service notifications"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startLocationUpdates() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            locationManager.requestLocationUpdates(
                android.location.LocationManager.GPS_PROVIDER,
                2000L,
                10f
            ) { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                locationTrackerManager.addLocation(latLng)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun showPermissionErrorAndStop() {
        Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LocationUpdateService::class.java)
        }
    }
}