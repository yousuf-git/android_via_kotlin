package com.learning.alarmapp

data class Alarm(
    val time: Long,
    val ringtoneUri: String,
    val id: Long = System.currentTimeMillis()
)

