package com.example.jsync.core.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object timeHelper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return Instant.ofEpochMilli(System.currentTimeMillis())
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(formatter)
    }
    fun formatDate(millis: Long?): String {
        val time = millis ?: System.currentTimeMillis()
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(Date(time))
    }

    fun formatDay(millis: Long?): String {
        val time = millis ?: System.currentTimeMillis()
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(Date(time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeStampToTime(timestamp: Long): String {
        val localTime = java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalTime()

        return java.time.format.DateTimeFormatter.ofPattern("HH : mm")
            .format(localTime)
    }
}