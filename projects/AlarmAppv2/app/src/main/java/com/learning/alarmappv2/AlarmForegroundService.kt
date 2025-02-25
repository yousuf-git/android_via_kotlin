package com.learning.alarmappv2

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmName = intent?.getStringExtra("alarm_name") ?: "Alarm"
        val ringtoneUri = Uri.parse(
            intent?.getStringExtra("ringtone_uri") ?:
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
        )

        // Start the activity
//        val alarmIntent = Intent(this, AlarmActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            putExtra("alarm_name", alarmName)
//            putExtra("ringtone_uri", ringtoneUri.toString())
//        }
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        alarmIntent.putExtra("ringtone_uri", ringtoneUri.toString())
        alarmIntent.putExtra("alarm_name", alarmName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create Notification
        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Alarm Ringing")
            .setContentText(alarmName)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
