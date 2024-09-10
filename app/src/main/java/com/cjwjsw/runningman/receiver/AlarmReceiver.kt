package com.cjwjsw.runningman.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.water.watersetting.WaterSettingActivity

@SuppressLint("RestrictedApi")
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "알람 수신됨: ${intent.getLongExtra("intervalMillis", 0L)}ms 후")
        // 알림 채널 설정
        val channelId = "water_reminder_channel"
        val channelName = "Water Reminder"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 설정
        val notificationIntent = Intent(context, WaterSettingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.water)
            .setContentTitle("물 섭취 알림")
            .setContentText("물을 섭취할 시간입니다!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 알림 전송
        notificationManager.notify(0, notification)
    }
}