package com.example.jsync.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showSystemUi = true)
@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {ip ->
        Column(
            modifier = Modifier.fillMaxSize().padding(ip).padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth() , 
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Tuesday" , fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "12 Jan 2026" , color = Color.DarkGray)
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday , contentDescription = "Calender"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {}) {

            }
        }

    }
}