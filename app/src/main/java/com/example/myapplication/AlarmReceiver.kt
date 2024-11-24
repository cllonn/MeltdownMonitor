package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmMessage = intent?.getStringExtra("ALARM_MSG")

        Log.d("alarmManager", alarmMessage!!)
    }
}