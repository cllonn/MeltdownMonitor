package com.example.myapplication

import java.time.LocalDateTime

data class AlarmItem(
    val alarmTime: LocalDateTime,
    val message: String
)