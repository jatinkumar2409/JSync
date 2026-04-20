package com.example.jsync.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.MainActivity
import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.core.helpers.toTaskCompletionDto
import com.example.jsync.core.helpers.toTaskDto
import com.example.jsync.core.helpers.toTaskEntity
import com.example.jsync.core.helpers.toTaskForUi
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.WebsocketMessage
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import com.example.jsync.data.room.entities.TaskEntity
import com.example.jsync.data.websockets.impls.WebsocketState
import com.example.jsync.domain.tasks.repos.MainRepository
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TasksViewModel(
    private val networkObserver: NetworkObserver ,
  private val mainRepo : MainRepository ,private val prefDatastore: prefDatastore ,private val webSocketsRepo: WebSocketsRepo ,
    private val taskCompletionRepo: TaskCompletionRepo
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val error_ = MutableStateFlow("")
    val error = error_.asStateFlow()
    val websocketState = webSocketsRepo.websocketState as StateFlow
    init {
        getAllTasks()
        loadTasksWithNetworkStatus()
    }
    fun getAllTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.getAllTasks().collect { tasks ->
                Log.d("tasksTag"  , "All tasks are $tasks")
            }
        }
    }
    fun loadTasksWithNetworkStatus(){
        viewModelScope.launch(Dispatchers.IO){
            networkObserver.observeNetwork().collect { it ->
                if (it){
                    loadTasks()
                }
            }
        }
    }
    fun loadTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            val toBeDeletedTasks = prefDatastore.getToBeDeletedTasks().first()
            isLoading.value = true
            mainRepo.loadTasksFromServer(toBeDeletedTasks) {
                error_.value = it
            }
            isLoading.value = false
        }
    }

    fun addTask(taskDTO: TaskDTO , selectedDate : Long , onError : (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepo.addTask(taskDTO.toTaskEntity(
                    SYNC_STATE.TO_BE_CREATED
                ).copy(
                    belongsToDate = selectedDate
                ))
                if(websocketState.value == WebsocketState.CONNECTED){
                    webSocketsRepo.sendTask(
                        WebsocketMessage(
                            type = "task", task = taskDTO
                        )
                    )
                }
            }catch (e : Exception){
                onError(e.message.toString())
            }
        }
    }
    fun deleteTask(taskDTO: TaskDTO , onError: (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepo.deleteTask(taskDTO.toTaskEntity(syncState = SYNC_STATE.TO_BE_DELETED))
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "delete_task", task = taskDTO
                    )
                )
            }catch (e : Exception){
                onError(e.message.toString())
            }
        }
    }
    fun retryTask(task: TaskEntity, taskCompletion: TaskCompletion? = null) {
        Log.d("tag24", "task is $task")
        if (task.syncState !in listOf(
                SYNC_STATE.TO_ADD_TASK_COMPLETION,
                SYNC_STATE.TO_DELETE_TASK_COMPLETION
            )
        ) {

            viewModelScope.launch(Dispatchers.IO) {
                val locked = mainRepo.retryTask(
                    task = task
                )
                if (locked == 1) {
                    webSocketsRepo.sendTask(
                        WebsocketMessage(
                            type = when (task.syncState) {
                                SYNC_STATE.TO_BE_CREATED -> "task"
                                SYNC_STATE.TO_BE_UPDATED -> "update_task"
                                SYNC_STATE.TO_BE_DELETED -> "delete_task"
                                else -> return@launch
                            }, task = task.toTaskDto()
                        )
                    )
                }
            }
        } else {
            if (taskCompletion == null) return
            viewModelScope.launch(Dispatchers.IO) {
                val locked = taskCompletionRepo.updateTaskCompletionStateIfUnchanged(
                    fromState = taskCompletion.syncState,
                    toState = SYNC_STATE.SYNCING,
                    id = taskCompletion.id
                )
                Log.d("tag24", "Sending task completion $taskCompletion")
                if (locked == 1) {
                    webSocketsRepo.sendTask(
                        WebsocketMessage(
                            type = "task_completion",
                            taskCompletion = taskCompletion.toTaskCompletionDto()
                        )
                    )
                }
            }
        }
    }
}