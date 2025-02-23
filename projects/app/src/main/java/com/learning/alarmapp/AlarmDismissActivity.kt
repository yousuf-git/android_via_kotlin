package com.learning.alarmapp

import android.media.Ringtone
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.learning.alarmapp.databinding.ActivityAlarmDismissBinding

class AlarmDismissActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmDismissBinding
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmDismissBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ringtone = intent.getSerializableExtra("ringtone") as? Ringtone

        binding.dismissButton.setOnClickListener {
            ringtone?.stop()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
    }
}

