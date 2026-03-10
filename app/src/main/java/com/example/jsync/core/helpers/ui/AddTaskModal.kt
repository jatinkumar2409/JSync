package com.example.jsync.core.helpers.ui

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80
import java.time.LocalTime

//@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(onDismiss: () -> Unit = {}, onAddTag: (String) -> Unit = {}, tagsList : List<String> = listOf("Meeting" , "Home" , "General" , "Undone")) {
    var taskName by remember {
        mutableStateOf("")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var tags = remember { mutableStateListOf<String>() }
    val colors = remember {
        listOf(
            blue40,
            Color.Red,
            Color.Green,
            Color.Magenta,
            Color.Cyan,
            Color.Yellow,
            Color.DarkGray
        )
    }
    var prioritySelection by remember {
        mutableIntStateOf(1)
    }
    var taskTypeSelection by remember {
        mutableIntStateOf(1)
    }
    var priorities = listOf("Low" , "Medium" , "High")
    var taskTypes = listOf("Temporary" , "Default" , "Recurring")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentWindowInsets = {
            WindowInsets(0.dp)
        },
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.5f))
        }
    ) {
        if (showDialog){
            AddTagDialog(onDismiss = { showDialog = false} , onAddTag = {
                onAddTag(it)
                showDialog = false
            } , tagsList = tagsList)

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(blue80, blue20, blue40)
                    ),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 40.dp)
        ) {

            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = {
                    Text(text = "What's need to be done ?" , color = Color.White , fontSize = 24.sp)
                },
                textStyle = TextStyle(
                    fontSize = 24.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth() ,
                horizontalArrangement = Arrangement.spacedBy(8.dp) ,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                 tagsList.forEach { tag ->
                     val selected = tag in tags
                     val color = remember {
                         colors.random()
                     }
                     Box(
                         modifier = Modifier
                             .clip(shape = RoundedCornerShape(4.dp))
                             .background(color)
                             .border(
                                 width = if (selected) 4.dp else 0.dp,
                                 color = blue80
                             )
                             .clickable {
                                 if (selected) {
                                     tags.remove(tag)
                                 } else {
                                     tags.add(tag)
                                 }
                             }
                     ) {
                         Text(text = tag , fontSize = 16.sp , modifier = Modifier.padding(vertical = 6.dp , horizontal = 8.dp))
                     }


                 }
                Box(
                    modifier =  Modifier
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(blue40)
                        .clickable {
                            showDialog = true
                        }

                ){
                    Text(text ="+" , fontSize = 16.sp , modifier = Modifier.padding(vertical = 6.dp , horizontal = 16.dp))

                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row() {
                Text(text = "Priority" , fontSize = 20.sp , fontWeight = FontWeight.Bold , color = Color.White)

            }
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                priorities.forEachIndexed { index, s ->
                  Row(verticalAlignment = Alignment.CenterVertically) {
                     RadioButton(selected = prioritySelection == index , onClick = {prioritySelection = index} , colors = RadioButtonDefaults.colors(
                      blue40
                     ) )
                     Text(text = s , fontSize = 16.sp , color = Color.White)
                      Spacer(modifier = Modifier.width(8.dp))
                  }
            }


        }
            Spacer(modifier = Modifier.height(24.dp))
            Row() {
                Text(text = "Task Type" , fontSize = 20.sp , fontWeight = FontWeight.Bold , color = Color.White)
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                taskTypes.forEachIndexed { index, s ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = taskTypeSelection == index , onClick = {prioritySelection = index} , colors = RadioButtonDefaults.colors(
                            blue40
                        ) )
                        Text(text = s , fontSize = 16.sp , color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }


            }
            Spacer(modifier = Modifier.height(24.dp))
            Row() {
                Text(text = "Due Time" , fontSize = 20.sp , fontWeight = FontWeight.Bold , color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth() , 
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    } , colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White ,
                        contentColor = blue40
                    )
                ){
                    Text(text = "Add" , fontSize = 20.sp)
                }
            }

    }
}
}
@Composable
fun AddTagDialog(onDismiss: () -> Unit , onAddTag : (String) -> Unit  , tagsList : List<String>) {
    Dialog(onDismissRequest = onDismiss) {
        var tagName by remember {
            mutableStateOf("")
        }
         Box(
             modifier = Modifier.fillMaxWidth()
         ){
             Card(
                 modifier = Modifier.fillMaxWidth() ,
                 colors = CardDefaults.cardColors(
                     containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
                 )
                 )
              {
                 Column(
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Spacer(modifier = Modifier.height(8.dp))
                     Text(
                         text = "Add Tag" , fontSize = 24.sp , fontWeight = androidx.compose.ui.text.font.FontWeight.Bold  , color = blue40
                     )
                     Spacer(modifier = Modifier.height(8.dp))
                     OutlinedTextField(
                         value = tagName , onValueChange = {tagName = it} , shape = RoundedCornerShape(12.dp),
                         modifier = Modifier.fillMaxWidth() , colors = OutlinedTextFieldDefaults.colors(
                             focusedBorderColor = blue40 ,
                             unfocusedBorderColor = blue40 ,
                             cursorColor = blue40

                         )

                     )
                     Row(modifier = Modifier) {
                         TextButton(onClick = onDismiss , colors = ButtonDefaults.textButtonColors(
                             containerColor = Color.Transparent , contentColor = blue40
                         )) {
                             Text(text = "Cancel")
                         }
                         TextButton(onClick = {
                             if (tagName.trim().isNotEmpty() && tagName !in tagsList) {
                                 onAddTag(tagName)
                             }
                             onDismiss()
                         } , colors = ButtonDefaults.textButtonColors(
                             containerColor = Color.Transparent , contentColor = blue40
                         )) {
                             Text(text = "Add")
                         }

                     }
                     Spacer(modifier = Modifier.height(8.dp))
                 }
              }
         }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TimePickerComp(onDismiss : () -> Unit = {}) {
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour ,
        initialMinute = LocalTime.now().minute ,
        is24Hour = true
    )
    TimePicker(state = timePickerState)
}
