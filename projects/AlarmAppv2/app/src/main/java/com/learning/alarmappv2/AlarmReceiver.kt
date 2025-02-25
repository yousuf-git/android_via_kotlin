package com.learning.alarmappv2

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Wake up the device
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
//            PARTIAL_WAKE_LOCK | FULL_WAKE_LOCK
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "AlarmApp:AlarmWakeLock"
        )
//        wakeLock.acquire(60 * 1000L) // 1 minute timeout
        wakeLock.acquire(5 * 60 * 1000L)           // 5 minutes timeout
        // Get ringtone URI and alarm name from intent
        val ringtoneUri = Uri.parse(intent.getStringExtra("ringtone_uri") ?:
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
        val alarmName = intent.getStringExtra("alarm_name") ?: "Alarm"


        // Start vibration
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationPattern = longArrayOf(0, 1000, 1000) // Start immediately, vibrate 1s, pause 1s
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0))
        }

        // Play ringtone
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, ringtoneUri)
                // setVolume(1.0f, 1.0f)
                // using less volume than above
                setVolume(0.1f, 0.1f)

                isLooping = true
                prepare()
                start()
            }

            // Show notification
            Toast.makeText(context, alarmName, Toast.LENGTH_LONG).show()

            // Start AlarmActivity to show dismiss button
            val alarmIntent = Intent(context, AlarmActivity::class.java)
//             alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            alarmIntent.putExtra("ringtone_uri", ringtoneUri.toString())
            alarmIntent.putExtra("alarm_name", alarmName)
//             context.startActivity(alarmIntent)
            // using pending intent to start activity
        // - bcz when phone is locked, we can't start activity directly
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
//            pendingIntent.send()
            try {
                pendingIntent.send()
//                Log.d("AlarmReceiver", "AlarmActivity started successfully!")
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
            // Start Foreground Service on Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val serviceIntent = Intent(context, AlarmForegroundService::class.java)
                serviceIntent.putExtra("alarm_name", alarmName)
                serviceIntent.putExtra("ringtone_uri", ringtoneUri.toString())
                context.startForegroundService(serviceIntent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            wakeLock.release()
        }
    }

    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var vibrator: Vibrator? = null
        fun stopAlarm() {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            vibrator?.cancel()
            vibrator = null
        }
    }
//    companion object {
//        fun stopAlarm(alarmReceiver: AlarmReceiver) {
//            alarmReceiver.mediaPlayer?.apply {
//                if (isPlaying) {
//                    stop()
//                }
//                release()
//            }
//            alarmReceiver.mediaPlayer = null
//            alarmReceiver.vibrator?.cancel()
//            alarmReceiver.vibrator = null
//        }
//    }
}