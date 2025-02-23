package com.learning.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.alarmapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarms = mutableListOf<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCurrentTime()
        setupAlarmList()
        setupSetAlarmButton()
    }

    private fun setupCurrentTime() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        binding.currentTimeTextView.text = timeFormat.format(Date())

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                binding.currentTimeTextView.text = timeFormat.format(Date())
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)
    }

    private fun setupAlarmList() {
        alarmAdapter = AlarmAdapter(alarms) { position -> deleteAlarm(position) }
        binding.alarmRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alarmAdapter
        }
    }

    private fun setupSetAlarmButton() {
        binding.setAlarmButton.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, hourOfDay, minute ->
            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0) // Ensure seconds are set to 0
            }
            // If the alarm time is in the past, set it for the next day
            if (alarmTime.before(Calendar.getInstance())) {
                alarmTime.add(Calendar.DAY_OF_MONTH, 1)
            }

            showRingtonePickerDialog(alarmTime)
        }, hour, minute, false).show()
    }

    private fun showRingtonePickerDialog(alarmTime: Calendar) {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Ringtone")
        startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val ringtoneUri = data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (ringtoneUri != null) {
                val alarmTime = Calendar.getInstance()
                addAlarm(alarmTime, ringtoneUri)
            }
        }
    }

    private fun addAlarm(alarmTime: Calendar, ringtoneUri: android.net.Uri) {
        val alarm = Alarm(alarmTime.timeInMillis, ringtoneUri.toString())
        alarms.add(alarm)
        alarmAdapter.notifyItemInserted(alarms.size - 1)

        setAlarm(alarm)

//        Toast.makeText(this, "Alarm set for ${} ${formatTime(alarmTime)}", Toast.LENGTH_SHORT).show()
//        date and time in toast
        Toast.makeText(this, "Alarm set for ${SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(alarmTime.time)}", Toast.LENGTH_SHORT).show()
    }

//    private fun setAlarm(alarm: Alarm) {
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, AlarmReceiver::class.java).apply {
//            putExtra(EXTRA_RINGTONE_URI, alarm.ringtoneUri)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        try {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
//        } catch (e : Exception) {
//            Toast.makeText(this, "Error setting alarm", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//    }
    private fun setAlarm(alarm: Alarm) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if the app can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Prompt the user to grant the permission
            val intent = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
            Toast.makeText(this, "Please grant permission to set exact alarms", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_RINGTONE_URI, alarm.ringtoneUri)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        try {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
            // Set the alarm using alarmTime
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarm.time,
                pendingIntent
            )
            Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied: Cannot set exact alarms", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting alarm", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAlarm(position: Int) {
        val alarm = alarms[position]
        alarms.removeAt(position)
        alarmAdapter.notifyItemRemoved(position)

        // Cancel the alarm
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)

        Toast.makeText(this, "Alarm deleted", Toast.LENGTH_SHORT).show()
    }

    private fun formatTime(calendar: Calendar): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    companion object {
        const val RINGTONE_PICKER_REQUEST_CODE = 1
        const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
    }
}

