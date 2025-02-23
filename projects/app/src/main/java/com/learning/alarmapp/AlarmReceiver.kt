package com.learning.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        val ringtoneUri = intent.getStringExtra(MainActivity.EXTRA_RINGTONE_URI)
//            ?.let { Uri.parse(it) }
//            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val ringtoneUri = Uri.parse("/sdcard/test_ring.mp3")
        val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//        toast of ringtone uri
//        adding ringtoneUri on adb logs
        // Log the ringtone URI
//        Log.d("AlarmReceiver", "------------Ringtone URI: $ringtoneUri")

        Toast.makeText(context, ringtoneUri.toString(), Toast.LENGTH_SHORT).show()
        ringtone.play()

        // start activity to show alarm dismissal screen
        val alarmIntent = Intent(context, AlarmDismissActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("ringtone_uri", ringtoneUri)
        }
        context.startActivity(alarmIntent)
    }
}
//
///////////////////
//package com.learning.alarmapp
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.core.content.ContextCompat
//
//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val ringtoneUri = Uri.parse("/sdcard/test_ring.mp3")
//
//        // Check if the app has permission to read external storage
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            Log.e("AlarmReceiver", "Permission to read external storage is not granted")
//            return
//        }
//
//        try {
//            val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//            if (ringtone == null) {
//                Log.e("AlarmReceiver", "Ringtone is null, unable to play")
//            } else {
//                // Log the ringtone URI
//                Log.d("AlarmReceiver", "Ringtone URI: $ringtoneUri")
//                Toast.makeText(context, ringtoneUri.toString(), Toast.LENGTH_SHORT).show()
//                ringtone.play()
//            }
//        } catch (e: Exception) {
//            Log.e("AlarmReceiver", "Error playing ringtone: ${e.message}")
//        }
//
//        // Start activity to show alarm dismissal screen
//        val alarmIntent = Intent(context, AlarmDismissActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            putExtra("ringtone_uri", ringtoneUri)
//        }
//        context.startActivity(alarmIntent)
//    }
//}