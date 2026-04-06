package com.example.jsync.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.core.helpers.toTaskDto
import com.example.jsync.core.helpers.toTaskEntity
import com.example.jsync.core.helpers.toTaskForUi
import com.example.jsync.core.helpers.ui.AddTaskModal
import com.example.jsync.core.helpers.ui.BinaryDialog
import com.example.jsync.core.helpers.ui.JSyncDatePicker
import com.example.jsync.core.helpers.ui.LoadingScreen
import com.example.jsync.core.helpers.ui.SwipeToRevealItem
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel : MainViewModel) {
    val tasksOfDate by viewModel.tasksOfDate.collectAsStateWithLifecycle()
    val tasks by remember(tasksOfDate) {
        derivedStateOf { tasksOfDate.map { it.toTaskForUi() } }
    }
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val tasksFromRoom by viewModel.tasksFromRoom.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if(networkStatus){
            Log.d("tag" , "load tasks has called")
            viewModel.loadTasks()
        }

    }
    Log.d("tag1" , "tasks from room is ${tasks.joinToString(" , ")}")
    Log.d("tag4" , "tasks from room is ${tasksFromRoom.joinToString(" , ")}")

    var searchTodos by remember{
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    var showBinaryDialog by remember {
        mutableStateOf(false)
    }
    var currentTask by remember{
        mutableStateOf<TaskDTO?>(null)
    }
    var failedDeleteBinaryDialog by remember {
        mutableStateOf(false)
    }
    val selTags = viewModel.selectedTags
    var showCalenderDialog by remember {
        mutableStateOf(false)
    }
    val filteredTasks by remember {
        derivedStateOf {
            if (selTags.isEmpty()) tasks
            else tasks.filter { task ->
                task.tags.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .any { it in selTags }
            }
        }
    }
    val error by viewModel.error.collectAsStateWithLifecycle()
    val context = LocalContext.current
    if(showCalenderDialog){
        Log.d("tag123" , "Show calender dialog is true")
        JSyncDatePicker(onDismiss = {
            showCalenderDialog = false
        }){
            if(it == null) return@JSyncDatePicker
            viewModel.updateSelectedDate(it)
            showCalenderDialog = false
        }
    }
    if(showBinaryDialog){
        BinaryDialog(
            onFirstClick = {
                showBinaryDialog = false
                currentTask = null
            } , onSecondClick = {
                if(currentTask == null) return@BinaryDialog
                 viewModel.deleteTask(
                     currentTask!!
                 ){
                     Toast.makeText(context, "Error : $it", Toast.LENGTH_SHORT).show()
                 }

                showBinaryDialog = false
                currentTask = null
            }
        )
    }
    if(failedDeleteBinaryDialog){
        BinaryDialog(
            mainText = "Failed to delete task" , firstButtonText = "Keep it" , secondButtonText = "Delete for me" ,
            onFirstClick = {
                viewModel.upsertTaskLocally(currentTask!!.toTaskEntity(
                    syncState = SYNC_STATE.SYNCED
                ))
                failedDeleteBinaryDialog = false
                currentTask = null
            } , onSecondClick = {
                viewModel.deleteTaskLocally(currentTask!!.toTaskEntity(
                    syncState = SYNC_STATE.TO_BE_DELETED
                ))
                failedDeleteBinaryDialog = false
                currentTask = null
            }
        )
    }
    LaunchedEffect(error) {
        if (error.trim().isEmpty()) return@LaunchedEffect
        Toast.makeText(context, "Error : $error", Toast.LENGTH_SHORT).show()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize() , floatingActionButton = {
            IconButton(onClick =  {
                showBottomSheet = true
            } , colors = IconButtonDefaults.iconButtonColors(
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
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize() ,
            isRefreshing = false , onRefresh ={
                if (networkStatus){
                    viewModel.loadTasks()
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ip)
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
                    LoadingScreen()
                } else {
                    if (showBottomSheet) {
                        if (currentTask == null) {
                            AddTaskModal(
                                onDismiss = { showBottomSheet = false },
                                onAddTag = {
                                    viewModel.addTag(it)
                                },
                                onAddTask = { task ->
                                    showBottomSheet = false

                                    viewModel.addTask(task){
                                        Toast.makeText(context, "Error : $it", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                },
                                tagsList = viewModel.tags, date = selectedDate
                            )
                        } else {
                            val task__ = currentTask!!
                            AddTaskModal(
                                task_ = task__,
                                onDismiss = {
                                    showBottomSheet = false
                                    currentTask = null
                                },
                                onAddTag = {
                                    viewModel.addTag(it)
                                }, onAddTask = { task ->
                                    showBottomSheet = false
                                    currentTask = null

                                    viewModel.updateTask(task){
                                        Toast.makeText(context, "Error : $it", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }, tagsList = viewModel.tags , date = selectedDate
                            )

                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = timeHelper.formatDay(selectedDate), fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = timeHelper.formatDate(selectedDate), color = Color.DarkGray)
                        }
                        IconButton(onClick = {
                            showCalenderDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Calender"
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
                            placeholder = { Text("Search...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = blue40,
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
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(
                            items = viewModel.tags
                        ) { tag ->
                            Card(
                                modifier = Modifier, colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                ), border = if (tag in selTags) {
                                    BorderStroke(
                                        width = 2.dp,
                                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                    )
                                } else null
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    blue20, blue40, blue80
                                                )
                                            )
                                        )
                                        .clickable {
                                            if (tag in selTags) selTags.remove(tag)
                                            else selTags.add(tag)
                                        }
                                ) {
                                    Text(
                                        text = tag, modifier = Modifier.padding(
                                            4.dp
                                        )
                                    )
                                }
                            }
                        }

                    }
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredTasks
                        ) { task ->
                            if (selTags.isEmpty() || task.tags.split(",").any { it in selTags }) {
                                SwipeToRevealItem(
                                    taskForUI = task,
                                    onFailedDeleteWarningClicked = {
                                        currentTask = tasksFromRoom.first { it.id == task.id }.toTaskDto()
                                        failedDeleteBinaryDialog = true
                                    }
                                    , onDelete = {
                                        currentTask = tasksFromRoom.first { it.id == task.id }.toTaskDto()
                                        showBinaryDialog = true
                                    }, onSyncClicked = {
                                        if (networkStatus) {
                                            viewModel.retryTask(tasksFromRoom.first { it.id == task.id })
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Connect to internet to sync this task",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }, onEdit = {
                                            currentTask = tasksFromRoom.first { it.id == task.id }.toTaskDto()
                                            Log.d("tag" , "Current id is $task")
                                            Log.d("tag"  , "all tasks are ${tasksFromRoom.joinToString(" , ")}")
                                            showBottomSheet = true
                                    }
                                ) { it ->
                                    val currentTask =  tasksFromRoom.first { it.id == task.id }.toTaskDto()
                                    if(task.type != 2) {
                                        viewModel.updateTask(
                                           currentTask
                                                .copy(
                                                    hasDone = it
                                                )
                                        ) { e ->
                                            Toast.makeText(
                                                context,
                                                "Error :$e",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }
                                    else{
                                        if(it){
                                            viewModel.addTaskCompletion(currentTask){ e ->
                                                Toast.makeText(context, "Error :$e", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                        else{
                                            viewModel.deleteTaskCompletion(currentTask){ e->
                                                Toast.makeText(
                                                    context,
                                                    "Error :$e",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

