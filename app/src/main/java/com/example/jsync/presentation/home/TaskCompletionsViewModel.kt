package com.example.jsync.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.core.helpers.toTaskCompletion
import com.example.jsync.core.helpers.toTaskCompletionDto
import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.WebsocketMessage
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class TaskCompletionsViewModel(
   private val taskCompletionRepo: TaskCompletionRepo , private val webSocketsRepo: WebSocketsRepo
) : ViewModel() {
    init {
        getAllTaskCompletions()
    }
    fun getAllTaskCompletions() {
        viewModelScope.launch(Dispatchers.IO) {
            taskCompletionRepo.getAllTaskCompletions().collect { taskCompletions ->
                Log.d("tasksTag", "All task completions are $taskCompletions")
            }
        }
    }
    fun addTaskCompletion(taskDTO: TaskDTO, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskCompletionId = UUID.randomUUID().toString()
                val taskCompletionDto = TaskCompletionDTO(
                    id = taskCompletionId,
                    taskId = taskDTO.id,
                    completionDate = taskDTO.belongsToDate,
                    isDeleted = false
                )
                taskCompletionRepo.addTaskCompletion(
                    taskCompletionDto.toTaskCompletion(
                        syncState = SYNC_STATE.TO_BE_CREATED
                    )
                )
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "task_completion", taskCompletion = taskCompletionDto
                    )
                )
            } catch (e: Exception) {
                onError(e.message.toString())
            }
        }
    }
    fun deleteTaskCompletion(taskCompletion: TaskCompletion, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskCompletionRepo.deleteTaskCompletion(
                    taskCompletion
                )
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "delete_task_completion",
                        task = null,
                        taskCompletion = taskCompletion.toTaskCompletionDto()
                    )
                )
            } catch (e: Exception) {
                onError(e.message.toString())
            }
        }

    }
}