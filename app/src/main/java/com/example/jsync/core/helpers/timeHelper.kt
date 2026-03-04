package com.example.jsync.core.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object timeHelper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateOfToday() : String{
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        val formattedDate = today.format(formatter)
        return formattedDate
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay(inputDate : String) : String{
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy" , Locale.ENGLISH)
        val date = LocalDate.parse(inputDate, formatter)

        val dayName = date.dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.getDefault())
      return dayName
    }
}