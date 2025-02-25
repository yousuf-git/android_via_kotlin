package com.learning.alarmappv2

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var dateTimePickerButton: Button
    private lateinit var ringtonePickerButton: Button
    private lateinit var setAlarmButton: Button
    private lateinit var setTestAlarmButton: Button     // for testing
    private lateinit var alarmsContainer: LinearLayout

    private lateinit var alarmName : EditText
    private var selectedRingtoneUri: String? = null
    private var selectedDateTime: Calendar? = null

    private val alarms = mutableListOf<AlarmInfo>()
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//    private val clockHandler = android.os.Handler() // deprecated in API 30
    private  val clockHandler = android.os.Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime()
            clockHandler.postDelayed(this, 1000)
        }
    }

     private val sharedPreferences by lazy {
            getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
     }

    private lateinit var ringtonePickerLauncher: ActivityResultLauncher<Intent>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        startClock()        // Start the clock - for current time
        loadAlarms()        // Load alarms from SharedPreferences
        updateAlarmsList()  // Update alarms list

    }

    // Load alarms from SharedPreferences
    private fun loadAlarms() {
        val alarmsJson = sharedPreferences.getString("alarms", null)
        if (alarmsJson != null) {
            val type = object : TypeToken<List<AlarmInfo>>() {}.type
            alarms.addAll(Gson().fromJson(alarmsJson, type))
        }
    }

    // On start, load the alarms from SharedPreferences
    private fun saveAlarms() {
        val alarmsJson = Gson().toJson(alarms)
        sharedPreferences.edit().putString("alarms", alarmsJson).apply()
        /*
        Sample alarms list in XML:
        <map>
            <string name="alarms">
                [
    	            {
                    "dateTime":
                            {
                                "year":2025,
                                "month":1,
                                "dayOfMonth":23,
                                "hourOfDay":18,
                                "minute":6,
                                "second":0
                            },
                    "name":"test",
			        "ringtoneUri":"content://media/internal/audio/media/10?title\u003dArgon\u0026canonical\u003d1"
		            }
	            ]
	        </string>
        </map>
         */
    }

    private fun initializeViews() {
        timeTextView = findViewById(R.id.timeTextView)
        dateTimePickerButton = findViewById(R.id.dateTimePickerButton)
        ringtonePickerButton = findViewById(R.id.ringtonePickerButton)
        setAlarmButton = findViewById(R.id.setAlarmButton)
        alarmsContainer = findViewById(R.id.alarmsContainer)
        alarmName = findViewById(R.id.alarmName)

        // Test button
        setTestAlarmButton = findViewById(R.id.setTestAlarmButton)
    }

    private fun setupClickListeners() {
        dateTimePickerButton.setOnClickListener { showDateTimePicker() }
        ringtonePickerButton.setOnClickListener { showRingtonePicker() }
        setAlarmButton.setOnClickListener { setAlarm() }
        setTestAlarmButton.setOnClickListener { setTestAlarm() }        // for testing
    }

    // for test alarm
    private fun setTestAlarm() {
//        set an alarm to ring after 30 seconds
        // name: Test Alarm
        // ringtone: default ringtone
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ringtone_uri", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
            putExtra("alarm_name", "Test Alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        selectedDateTime = Calendar.getInstance().apply {
            add(Calendar.SECOND, 10)
        }

        // Set exact alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            selectedDateTime!!.timeInMillis,
            pendingIntent
        )


        val newAlarm = AlarmInfo(
            "Test Alarm",
            Calendar.getInstance().apply { timeInMillis = selectedDateTime!!.timeInMillis },
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
        )

        alarms.add(newAlarm)
        saveAlarms()            // Save alarms to SharedPreferences
        updateAlarmsList()

        Toast.makeText(
            this,
            "Test Alarm set for ${dateFormat.format(selectedDateTime!!.time)} ${timeFormat.format(selectedDateTime!!.time)}",
            Toast.LENGTH_SHORT
        ).show()

        // Reset selections after setting alarm
        selectedDateTime = null
        selectedRingtoneUri = null
        dateTimePickerButton.text = "Select Date & Time"
        ringtonePickerButton.text = "Select Ringtone"
        alarmName.text.clear()
    }

    private fun startClock() {
        updateTimeRunnable.run()
    }

    private fun updateCurrentTime() {
        val currentTime = Calendar.getInstance().time
        timeTextView.text = timeFormat.format(currentTime)
        updateAlarmsList()
    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        selectedDateTime = Calendar.getInstance().apply {
                            set(year, month, day, hour, minute, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        dateTimePickerButton.text = "Selected: ${dateFormat.format(selectedDateTime!!.time)} ${timeFormat.format(selectedDateTime!!.time)}"
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    false
                ).show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone")
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri)
        }
        startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE) // deprecated in API 30
//        ringtonePickerLauncher.launch(intent)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedRingtoneUri = data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.toString() // deprecated in API 30
            ringtonePickerButton.text = "Ringtone Selected ✓"
        }
    }

    private fun setAlarm() {
        if (selectedDateTime == null) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedRingtoneUri == null) {
            Toast.makeText(this, "Please select a ringtone", Toast.LENGTH_SHORT).show()
            return
        }
//        if name is empty
        if (alarmName.text.isEmpty()) {
            Toast.makeText(this, "Please enter a name for the alarm", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if selected time is in the past
        if (selectedDateTime!!.before(Calendar.getInstance())) {
            Toast.makeText(this, "Cannot set alarm for past time", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for duplicate alarms
        if (alarms.any { it.dateTime.timeInMillis == selectedDateTime!!.timeInMillis }) {
            Toast.makeText(this, "An alarm already exists for this time", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the alarm
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ringtone_uri", selectedRingtoneUri)
            putExtra("alarm_name", alarmName.text.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            selectedDateTime!!.timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set exact alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            selectedDateTime!!.timeInMillis,
            pendingIntent
        )

        val newAlarm = AlarmInfo(
            alarmName.text.toString(),
            Calendar.getInstance().apply { timeInMillis = selectedDateTime!!.timeInMillis },
            selectedRingtoneUri!!,

        )
        alarms.add(newAlarm)
        saveAlarms()            // Save alarms to SharedPreferences
        updateAlarmsList()

        Toast.makeText(
            this,
            "Alarm set for ${dateFormat.format(selectedDateTime!!.time)} ${timeFormat.format(selectedDateTime!!.time)}",
            Toast.LENGTH_SHORT
        ).show()

        // Reset selections after setting alarm
        selectedDateTime = null
        selectedRingtoneUri = null
        dateTimePickerButton.text = "Select Date & Time"
        ringtonePickerButton.text = "Select Ringtone"
        alarmName.text.clear()
    }

    private fun updateAlarmsList() {
        alarmsContainer.removeAllViews()
        val currentTime = Calendar.getInstance()

        // Remove passed alarms
        alarms.removeAll { it.dateTime.before(currentTime) }

        // Sort alarms by time
        val sortedAlarms = alarms.sortedBy { it.dateTime.timeInMillis }

        sortedAlarms.forEach { alarm ->
            val alarmView = layoutInflater.inflate(R.layout.alarm_item, alarmsContainer, false)
            val timeText = alarmView.findViewById<TextView>(R.id.alarmTimeTextView)
            val nameText = alarmView.findViewById<TextView>(R.id.alarmNameTextView)

            nameText.text = alarm.name
            timeText.text = "${dateFormat.format(alarm.dateTime.time)} ${timeFormat.format(alarm.dateTime.time)}"

            // Highlight upcoming alarm
            if (alarm == sortedAlarms.first()) {

                alarmView.setBackgroundResource(R.drawable.upcoming_alarm_background)
                timeText.setTextColor(Color.WHITE)
                nameText.setTextColor(Color.WHITE)
//                set the clock icon to white
                val clockIcon = alarmView.findViewById<ImageView>(R.id.alarmIconImageView)
                clockIcon.setColorFilter(Color.WHITE)
            }

            alarmsContainer.addView(alarmView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clockHandler.removeCallbacks(updateTimeRunnable)
    }

    companion object {
        private const val RINGTONE_PICKER_REQUEST_CODE = 1
    }
}

data class AlarmInfo(
    val name : String,
    val dateTime: Calendar,
    val ringtoneUri: String
)