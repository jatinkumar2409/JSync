package com.example.jsync.core.helpers.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80
@Preview(showBackground = true)
@Composable
fun BinaryDialog(
    mainText : String = "Are you sure to delete this task?" ,
    firstButtonText : String = "Cancel" , secondButtonText : String = "Delete" , onFirstClick : () -> Unit = {} , onSecondClick : () -> Unit = {} , onDismiss : () -> Unit = {}
) {
    Dialog(
        onDismissRequest = {}
    ) {
            Card(
                modifier = Modifier.fillMaxWidth() , colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    blue80 , blue20 , blue40
                                )
                            )
                        )
                    , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = mainText , fontSize = 20.sp , color = if(isSystemInDarkTheme()) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp) , horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onFirstClick
                        ) {
                            Text(
                                text = firstButtonText , color = if(isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                        TextButton(
                            onClick = onSecondClick
                        ) {
                            Text(
                                text = secondButtonText , color = if(isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
    }
}