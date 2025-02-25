package com.learning.alarmappv2

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.learning.alarmappv2.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {
//    access the alarm name text view from activity_alarm.xml
        private lateinit var timeTextView: TextView

//    private lateinit var binding: ActivityAlarmBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        // Show over lockscreen
//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize View Binding - for debug
//        binding = ActivityAlarmBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
//        setContentView(R.layout.activity_alarm)
//        // get alarm name from intent and display that
        timeTextView = findViewById<TextView>(R.id.alarmNameTxtView)
        timeTextView.text = intent.getStringExtra("alarm_name") ?: "Alarm"

        // Set up dismiss button
//        binding.dismissButton.setOnClickListener {
//            AlarmReceiver.stopAlarm()
//            finish()
//        }


        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            AlarmReceiver.stopAlarm()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmReceiver.stopAlarm() // if user simply closes the activity without clicking on dismiss button
    }
}