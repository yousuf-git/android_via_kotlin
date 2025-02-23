package com.learning.alarmappv2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {
//    access the alarm name text view from activity_alarm.xml
        private lateinit var timeTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
//    get alarm name from intent and display that
        timeTextView = findViewById<TextView>(R.id.alarmNameTxtView)
        timeTextView.text = intent.getStringExtra("alarm_name") ?: "Alarm"
//        val alarmName = intent.getStringExtra("alarm_name") ?: "Alarm"
//        title = alarmName


        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            AlarmReceiver.stopAlarm()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmReceiver.stopAlarm()
    }
}