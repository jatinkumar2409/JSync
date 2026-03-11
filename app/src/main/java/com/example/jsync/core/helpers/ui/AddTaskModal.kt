package com.example.jsync.core.helpers.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(
    onDismiss: () -> Unit = {},
    onAddTag: (String) -> Unit = {},
    tagsList: List<String> = listOf("Meeting", "Home", "General", "Undone")
) {

    var taskName by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val tags = remember { mutableStateListOf<String>() }

    val colors = remember {
        listOf(
            blue40,
            Color(0xFFFF6B6B),
            Color(0xFF6BCB77),
            Color(0xFF4D96FF),
            Color(0xFFFFD93D),
            Color(0xFFB983FF)
        )
    }

    var prioritySelection by remember { mutableIntStateOf(1) }
    var taskTypeSelection by remember { mutableIntStateOf(1) }
    var dueTime by remember { mutableStateOf<Long?>(null) }

    val priorities = listOf("Low", "Medium", "High")
    val taskTypes = listOf("Temporary", "Default", "Recurring")

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var timeDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentWindowInsets = { WindowInsets(0.dp) },
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.5f))
        }
    ) {

        if (showDialog) {
            AddTagDialog(
                onDismiss = { showDialog = false },
                onAddTag = {
                    onAddTag(it)
                    showDialog = false
                },
                tagsList = tagsList
            )
        }

        if (timeDialog) {
            TimePickerComp(
                onDismiss = { timeDialog = false }
            ) {
                dueTime = it
                timeDialog = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(blue80, blue40, blue20)
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = {
                    Text(
                        "What do you need to do?",
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                tagsList.forEach { tag ->

                    val selected = tag in tags
                    val color = remember { colors.random() }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) color else color.copy(alpha = 0.35f)
                            )
                            .border(
                                width = if (selected) 2.dp else 0.dp,
                                color = Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(50)
                            )
                            .clickable {
                                if (selected) tags.remove(tag)
                                else tags.add(tag)
                            }
                    ) {
                        Text(
                            text = tag,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { showDialog = true }
                ) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Priority",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                priorities.forEachIndexed { index, s ->

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(
                            selected = prioritySelection == index,
                            onClick = { prioritySelection = index },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.White
                            )
                        )

                        Text(
                            text = s,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Task Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                taskTypes.forEachIndexed { index, s ->

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(
                            selected = taskTypeSelection == index,
                            onClick = { taskTypeSelection = index },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.White
                            )
                        )

                        Text(
                            text = s,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Due Time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { timeDialog = true },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = blue40
                    )
                ) {
                    Text("Pick")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    onClick = {},
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .fillMaxWidth(0.35f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = blue40
                    )
                ) {
                    Text(
                        text = "Add Task",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onAddTag: (String) -> Unit,
    tagsList: List<String>
) {

    Dialog(onDismissRequest = onDismiss) {

        var tagName by remember { mutableStateOf("") }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Add Tag",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = blue40
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = {
                            if (tagName.trim().isNotEmpty() && tagName !in tagsList) {
                                onAddTag(tagName)
                            }
                            onDismiss()
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComp(
    onDismiss: () -> Unit = {},
    onTimeSelected: (timestamp: Long) -> Unit = {}
) {

    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Due Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = blue40
                )

                Spacer(modifier = Modifier.height(12.dp))

                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute

                        val selectedTime = LocalTime.of(hour, minute)
                        val today = LocalDate.now()

                        val localDateTime = LocalDateTime.of(today, selectedTime)

                        val timestamp = localDateTime
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        Log.d("tag", timestamp.toString())

                        onTimeSelected(timestamp)
                    }
                ) {
                    Text("Add")
                }
            }
        }
    }
}