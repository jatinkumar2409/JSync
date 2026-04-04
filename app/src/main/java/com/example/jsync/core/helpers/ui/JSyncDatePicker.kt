package com.example.jsync.core.helpers.ui
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80
import java.util.Calendar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JSyncDatePicker( onDismiss : () -> Unit ,
    onDateSelected: (Long?) -> Unit
) {
    val currentTime = System.currentTimeMillis()

    val oneYearLater = Calendar.getInstance().apply {
        timeInMillis = currentTime
        add(Calendar.YEAR, 1)
    }.timeInMillis

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= oneYearLater
            }
        }
    )

    DatePickerDialog(
        colors = DatePickerDefaults.colors(

            // Header (month/year text)
            titleContentColor = blue80,
            headlineContentColor = blue80,

            // Week days (Mon, Tue...)
            weekdayContentColor = blue40,

            // Calendar dates
            dayContentColor = blue80,
            disabledDayContentColor = blue20,

            // Selected day
            selectedDayContentColor = androidx.compose.ui.graphics.Color.White,
            selectedDayContainerColor = blue40,

            // Today highlight
            todayContentColor = blue80,
            todayDateBorderColor = blue40,

            // Year picker
            yearContentColor = blue80,
            selectedYearContentColor = androidx.compose.ui.graphics.Color.White,
            selectedYearContainerColor = blue40,

            // Navigation icons (arrow)
            navigationContentColor = blue80
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {}) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}