package com.example.jsync.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80

//@Preview(showSystemUi = true)
@Composable
fun HomeScreen() {
    var searchTodos by remember{
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize() , floatingActionButton = {
            IconButton(onClick =  {} , colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent , contentColor = Color.White
            ) , modifier = Modifier
                .clip(shape = CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(blue20, blue40, blue80)
                    )
                )) {
                Icon(imageVector = Icons.Default.Add , contentDescription = "fab")
            }
        }
    ) {ip ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ip)
                .padding(horizontal = 16.dp)
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
            val buttonWeight by animateFloatAsState(
                targetValue = if (expanded) 0.2f else 1f,
                label = "buttonWeight"
            )

            val textFieldWeight by animateFloatAsState(
                targetValue = if (expanded) 0.8f else 0.0001f,
                label = "textFieldWeight"
            )

            val textFieldAlpha by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                label = "alpha"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = searchTodos,
                    onValueChange = { searchTodos = it },
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(textFieldWeight)
                        .alpha(textFieldAlpha),
                    singleLine = true,
                    placeholder = { Text("Search...") } ,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = blue40 ,
                        unfocusedIndicatorColor = blue20
                    )
                )

                Button(
                    onClick = { expanded = !expanded },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(buttonWeight)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(blue20, blue40, blue80)
                            ),
                            shape = CircleShape
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "search")

                        AnimatedVisibility(visible = !expanded) {
                            Text(
                                text = "Search",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

        }

    }

}
@Preview(showBackground = true)
@Composable
fun TaskItem(modifier: Modifier = Modifier , task : TaskDTO = TaskDTO(
    id = "1",
    taskName = "Sample Task",
    dueAt = System.currentTimeMillis(),
    type = 0,
    priority = 1,
    hasDone = true ,
    tags = "sample"
)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (task.hasDone) 0.dp else 4.dp
                ), colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (task.hasDone) Icons.Default.TaskAlt else Icons.Outlined.Task,
                        contentDescription = "tasks"
                    )
                    Text(
                        text = task.taskName,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "12:00 pm",
                        color = Color.DarkGray
                    )
                }
                    Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}