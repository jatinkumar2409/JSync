package com.example.jsync.presentation.about

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jsync.ui.theme.blue80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack : () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize() , topBar = {
        TopAppBar(
            title = {
                Text(text = "JSync")
            } , navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "back"
                    )
                }
            } , colors = TopAppBarDefaults.topAppBarColors(
                containerColor = blue80 , titleContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black  ,
                actionIconContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        )
    }) {
        ip ->
        Column(
            modifier = Modifier.fillMaxSize().padding(ip)  , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "JSync v1.0"
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text("Developed by : Jatin Kumar")
        }
    }
}