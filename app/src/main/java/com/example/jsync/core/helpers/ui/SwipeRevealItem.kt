package com.example.jsync.core.helpers.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.TaskForUi
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToRevealItem(taskForUI: TaskForUi , onFailedDeleteWarningClicked: () -> Unit , onSyncClicked : () -> Unit, onDelete : () -> Unit, onEdit : () -> Unit, onDoneToggle : (Boolean) -> Unit) {
    val offsetX = remember {
        Animatable(initialValue = 0f)
    }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val actionWidth = 128.dp
    val actionWidthPx = remember {
        with(density) { actionWidth.toPx() }
    }
    Box(modifier = Modifier.fillMaxWidth()){
        Card(modifier = Modifier.fillMaxWidth() ,
            colors = CardDefaults.cardColors(
                containerColor = if(isSystemInDarkTheme()) Color.DarkGray else Color.White
            )
            ) {
             Column(modifier = Modifier.fillMaxWidth()) {
                 Spacer(modifier = Modifier.height(4.dp))
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Row(
                         modifier = Modifier.width(actionWidth),
                         horizontalArrangement = Arrangement.Start
                     ) {
                         IconButton(onClick = {
                             onEdit()
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = Icons.Default.Edit,
                                 contentDescription = "edit",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                         IconButton(onClick = {
                             if (taskForUI.hasDone){
                                 onDoneToggle(false)
                             }
                             else{
                                 onDoneToggle(true)
                             }
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = if(taskForUI.hasDone) Icons.Outlined.Cancel else  Icons.Default.Check,
                                 contentDescription = "done or undone",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                         IconButton(onClick = {
                             onDelete()
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = Icons.Default.Delete,
                                 contentDescription = "delete",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                     }

                     Row(
                         modifier = Modifier.width(actionWidth),
                         horizontalArrangement = Arrangement.End
                     ) {
                         IconButton(onClick = {
                             onEdit()
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = Icons.Default.Edit,
                                 contentDescription = "edit",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                         IconButton(onClick = {
                             if (taskForUI.hasDone){
                                 onDoneToggle(false)
                             }
                             else{
                                 onDoneToggle(true)
                             }
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = if(taskForUI.hasDone) Icons.Outlined.Cancel else Icons.Default.Check,
                                 contentDescription = "done or undone",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                         IconButton(onClick = {
                             onDelete()
                             scope.launch {
                                 offsetX.animateTo(0f)
                             }
                         }) {
                             Icon(
                                 imageVector = Icons.Default.Delete,
                                 contentDescription = "delete",
                                 tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                             )
                         }
                     }
                 }
                 Spacer(modifier = Modifier.height(4.dp))
             }
       }
        TaskItem(
            task = taskForUI,
            offsetX = offsetX ,
            scope = scope ,
            actionWidthPx = actionWidthPx ,
            onSyncClicked = onSyncClicked ,
            onFailedDeleteWarningClicked = onFailedDeleteWarningClicked
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItem(task : TaskForUi = TaskForUi(
    id = "",
    taskName = "My task",
    dueAt = 0L,
    type = 1,
    priority = 1,
    hasDone = false,
    tags = "",
    syncState = SYNC_STATE.FAILED_DELETE
), offsetX : Animatable<Float , AnimationVector1D> = Animatable(1f), scope : CoroutineScope = rememberCoroutineScope(),
             actionWidthPx : Float = 0.0f, onSyncClicked : () -> Unit = {} , onFailedDeleteWarningClicked : () -> Unit = {}
) {
    Log.d("tag3" , "Sync state of ${task.taskName} is : ${task.syncState}")
    val rotation = if (task.syncState == SYNC_STATE.SYNCING) {
        val transition = rememberInfiniteTransition(label = "sync_rotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation_anim"
        ).value
    } else {
        0f
    }

    Box(modifier = Modifier
        .offset{
            IntOffset(
                offsetX.value.roundToInt() , 0
            )
        }
        .fillMaxWidth()
        .pointerInput(Unit){
            detectHorizontalDragGestures(
                onHorizontalDrag = { _ , dragAmount ->
                 scope.launch {
                     val newOffSet = (offsetX.value + dragAmount).coerceIn(-actionWidthPx , actionWidthPx)
                     offsetX.snapTo(newOffSet)
                 }
                } ,
                onDragEnd = {
                   val midWidth = actionWidthPx / 2
                    when{
                        offsetX.value > midWidth -> {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = actionWidthPx,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                        offsetX.value < -midWidth -> {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = -actionWidthPx,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                        else ->{
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    }
                }


            )
        }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (task.hasDone) 0.dp else 4.dp
            ), colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth().background(
                brush = Brush.linearGradient(
                    listOf(blue20, blue40 , blue80)
                )
            )) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (task.hasDone) Icons.Default.TaskAlt else Icons.Outlined.Task,
                            contentDescription = "tasks",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        Text(
                            text = task.taskName,
                            modifier = Modifier.fillMaxWidth(0.6f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (task.dueAt == null) "" else timeHelper.formatTimeStampToTime(
                                task.dueAt
                            ),
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        IconButton(onClick = {
                            if (task.syncState != SYNC_STATE.SYNCED && task.syncState != SYNC_STATE.SYNCING) {
                                onSyncClicked()
                            }
                            else if(task.syncState == SYNC_STATE.FAILED_DELETE){

                            }
                        }) {
                            Icon(
                                modifier = Modifier.rotate(rotation),
                                imageVector = when(task.syncState){
                                    SYNC_STATE.SYNCED -> Icons.Default.Done
                                    SYNC_STATE. FAILED_DELETE -> Icons.Default.Warning
                                    else -> Icons.Default.Autorenew
                                },
                                contentDescription = "sync",
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}