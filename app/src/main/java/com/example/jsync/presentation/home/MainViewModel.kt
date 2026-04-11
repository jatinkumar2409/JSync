package com.example.jsync.presentation.home

import android.util.Log
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.core.helpers.toTaskCompletion
import com.example.jsync.core.helpers.toTaskCompletionDto
import com.example.jsync.core.helpers.toTaskDto
import com.example.jsync.core.helpers.toTaskEntity
import com.example.jsync.core.helpers.toTaskForUi
import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.TaskForUi
import com.example.jsync.data.models.WebsocketMessage
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import com.example.jsync.data.room.entities.TaskEntity
import com.example.jsync.domain.tasks.repos.MainRepository
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import com.example.jsync.domain.tasks.repos.TaskRepository
import com.example.jsync.domain.tasks.usecases.AddTaskUseCase
import com.example.jsync.domain.tasks.usecases.LoadTasksUseCase
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val networkObserver: NetworkObserver ,
    private val taskRepo : TaskRepository , val prefDatastore: prefDatastore ,  private val webSocketsRepo: WebSocketsRepo, private val taskCompletionRepo: TaskCompletionRepo ,
    private val mainRepo : MainRepository
) : ViewModel() {
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus = _networkStatus.asStateFlow()
    val websocketState = webSocketsRepo.websocketState as StateFlow
    val isLoading = mutableStateOf(false)
    private val selectedDate_ = MutableStateFlow(System.currentTimeMillis())
    val selectedDate = selectedDate_.asStateFlow()

    val localTasks = MutableStateFlow<Map<String , TaskForUi>>(emptyMap())
    val taskCompletions = combine(
        selectedDate , prefDatastore.userId.filterNotNull()
    ){ date , userId ->
        date to userId
    }.flatMapLatest { (date , userId) ->
        taskCompletionRepo.getTaskCompletionsOfDate(date , userId)
    }.stateIn(
        viewModelScope , SharingStarted.WhileSubscribed(5000L) , emptyList()
    )
    val tasksFromRoom = prefDatastore.userId.filter { !(it.isNullOrEmpty() || it.trim().isEmpty()) }
        .flatMapLatest { userId ->
            mainRepo.getDisplayableTasks(userId!!)
        }.stateIn(
            viewModelScope , SharingStarted.WhileSubscribed(5000) , emptyList()
        )
    val tasks_ = merge(
        tasksFromRoom.map{tasks -> tasks.map { it.toTaskForUi() } } ,
        localTasks.map { it.values.toList() }
    ).stateIn(
        viewModelScope , SharingStarted.WhileSubscribed(5000) , emptyList()
    )
     val tasks = prefDatastore.userId
         .filter {
             !(it.isNullOrEmpty() || it.trim().isEmpty())
         }
         .flatMapLatest { userId ->
             Log.d("tag" , "i am in tasks stateflow")
         combine(
             mainRepo.getDisplayableTasks(userId!!) , localTasks
         ){ dbTasks , localMap ->
             dbTasks.map { task ->
                  localMap[task.id] ?: task.toTaskForUi().copy(

                  )
             }
         }
     }.stateIn(
         viewModelScope , SharingStarted.WhileSubscribed(5000) , emptyList()
     )

     val tags = mutableStateListOf("Undone")
     val selectedTags = mutableStateListOf<String>()
     val error_ = MutableStateFlow("")
     val error = error_.asStateFlow()


    val tasksOfDate = combine(
        selectedDate , prefDatastore.userId.filterNotNull()
    ){ date , userId ->
        date to userId
    }.flatMapLatest { (date , userId) ->
        Log.d("tag23" , "Flap map latest is running of date ${timeHelper.formatDate(date)} and userId is $userId")
       val tasksFlow =  mainRepo.getTasksOfDate(belongsToDate = date , userId = userId)
        val taskCompletionsFlow = taskCompletionRepo.getTaskCompletionsOfDate(date , userId)
         combine(tasksFlow , taskCompletionsFlow){ tasks , taskCompletions ->
            tasks.map { task ->
                Log.d("tag23" , "task is $task")
                val taskCompletion = taskCompletions.firstOrNull{ it.taskId == task.id }
                task.copy(hasDone = if(task.type == 2) task.id in taskCompletions
                        .map { it.taskId } else task.hasDone, syncState = if(taskCompletion == null) task.syncState else {
                            if(taskCompletion.syncState == SYNC_STATE.TO_BE_DELETED) SYNC_STATE.TO_DELETE_TASK_COMPLETION
                            if (taskCompletion.syncState == SYNC_STATE.TO_BE_CREATED) SYNC_STATE.TO_ADD_TASK_COMPLETION
                            else SYNC_STATE.SYNCED

                }
                )
            }
        }.combine(localTasks){ tasks , localTasks ->
            tasks.map { task ->
                localTasks[task.id] ?: task.toTaskForUi()
            }
         }
    }.stateIn(
        viewModelScope , SharingStarted.WhileSubscribed(5000) , emptyList()
    )

//    val tasksForUi by derivedStateOf {
//        tasksOfDate.map { tasks -> tasks.map {
//            it.toTaskForUi()
//        } }
//    }
   init {
       observeNetwork()
       connectToWebSocket()
       getNewTasks()
       loadTasksCompletionsOfDate()
       getAllTasks()
       getAllTaskCompletions()
   }
    fun updateSelectedDate(date : Long){
        selectedDate_.value = date
    }
    fun getAllTaskCompletions(){
        viewModelScope.launch(Dispatchers.IO) {
            taskCompletionRepo.getAllTaskCompletions().collect { taskCompletions ->
                Log.d("tasksTag"  , "All task completions are $taskCompletions")
            }
        }
    }

    fun getAllTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.getAllTasks().collect { tasks ->
                Log.d("tasksTag"  , "All tasks are $tasks")
            }
        }
    }
    fun loadTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            val toBeDeletedTasks = prefDatastore.getToBeDeleted().first()
            isLoading.value = true
            mainRepo.loadTasksFromServer(toBeDeletedTasks) {
                error_.value = it
            }
            isLoading.value = false
        }
    }
    fun loadTasksCompletionsOfDate(){
        viewModelScope.launch {
            try {
              selectedDate_.collect { date ->
                  taskCompletionRepo.loadTaskCompletionOfDateFromServer(date)
              }
            }
            catch (e : Exception){
                error_.value = e.message.toString()
            }
        }
    }

    fun upsertTaskLocally(task : TaskEntity){
        viewModelScope.launch {
            mainRepo.upsertSyncedTask(task)
        }
    }
    fun deleteTaskLocally(task : TaskEntity){
        viewModelScope.launch {
            mainRepo.deleteSyncedTask(task)
        }
    }
    fun changeTasksLocally(task : TaskForUi){
        localTasks.update { current ->
            current + (task.id to task)
        }
    }
    fun observeNetwork() {
        viewModelScope.launch(Dispatchers.IO){
            networkObserver.observeNetwork().collect { it ->
                _networkStatus.value = it
            }
        }
    }
    fun connectToWebSocket(){
        viewModelScope.launch {
            webSocketsRepo.connect(
                onError = {
                    error_.value = it
                }
            )
        }
    }
    fun addTask(taskDTO: TaskDTO , onError : (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepo.addTask(taskDTO.toTaskEntity(SYNC_STATE.TO_BE_CREATED).copy(
                    belongsToDate = selectedDate.value
                ))
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "task", task = taskDTO
                    )
                )
            }catch (e : Exception){
                onError(e.message.toString())
            }
        }
    }

    fun getNewTasks(){
        viewModelScope.launch(Dispatchers.IO) {
                webSocketsRepo.messages.collect { message ->
                    if (message.type == "task") {
                        val __task = message.task!!
                        Log.d("tag", "message value is $message")
                        mainRepo.upsertSyncedTask(__task.toTaskEntity())
                        changeTasksLocally(__task.toTaskForUi().copy(
                            syncState = SYNC_STATE.SYNCED
                        ))
                        val _tags = __task.tags.split(",")
                        tags.forEach { it ->
                            if (it !in tags) tags.add(it)
                        }
                    } else if (message.type == "delete_task") {
                        mainRepo.deleteSyncedTask(message.task!!.toTaskEntity())
                    } else if (message.type == "update_task") {
                       val newTask = message.task ?: return@collect
                        mainRepo.upsertSyncedTask(newTask.toTaskEntity())
                        changeTasksLocally(newTask.toTaskForUi().copy(
                            syncState = SYNC_STATE.SYNCED
                        ))
                    }
                    else if(message.type.startsWith("error_")){
                        error_.value = message.error!!
                        val prevSync = when(message.type){
                            "error_for_task" -> SYNC_STATE.TO_BE_CREATED
                            "error_for_update_task" -> SYNC_STATE.TO_BE_UPDATED
                            "error_for_delete_task" -> SYNC_STATE.TO_BE_DELETED
                            else -> null
                        }
                        prevSync?.let {
                            mainRepo.upsertSyncedTask(message.task?.toTaskEntity()!!.copy(
                                syncState = prevSync
                            ))
                            changeTasksLocally(
                                message.task?.toTaskForUi()!!.copy(
                                    syncState = prevSync
                                )
                            )
                        }
                        if(message.type == "error_for_task_completion"){

                        }

                    }
                    else if(message.type == "task_completion"){
                        val taskCompletion = message.taskCompletion!!
                        taskCompletionRepo.upsertSyncedTaskCompletion(taskCompletion.toTaskCompletion(
                            SYNC_STATE.SYNCED))
                    }
                    else if(message.type == "delete_task_completion"){
                        val taskCompletion = message.taskCompletion!!
                        taskCompletionRepo.deleteSyncedTaskCompletion(taskCompletion.toTaskCompletion())
                    }
                }

        }
    }
    fun addTaskCompletion(taskDTO: TaskDTO , onError: (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskCompletionId = UUID.randomUUID().toString()
                val taskCompletionDto = TaskCompletionDTO(
                    id = taskCompletionId , taskId = taskDTO.id , completionDate = taskDTO.belongsToDate , isDeleted = false
                )
                taskCompletionRepo.upsertSyncedTaskCompletion(TaskCompletion(
                    id = taskCompletionId, taskId = taskDTO.id , completionDate = taskDTO.belongsToDate  , syncState = SYNC_STATE.TO_BE_CREATED
                ))
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                            type = "task_completion" , taskCompletion = taskCompletionDto
                    )
                )
            } catch (e: Exception) {
                onError(e.message.toString())
            }
        }
    }
    fun deleteTaskCompletion(taskCompletion : TaskCompletion, onError: (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskCompletionRepo.upsertSyncedTaskCompletion(
                   taskCompletion.copy(syncState = SYNC_STATE.TO_BE_DELETED)
                )
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "delete_task_completion" , task = null , taskCompletion = taskCompletion.toTaskCompletionDto()
                    )
                )
            }
            catch (e : Exception){
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
    fun updateTask(taskDTO: TaskDTO  , onError : (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
              val task =  mainRepo.updateTask(taskDTO.toTaskEntity(syncState = SYNC_STATE.TO_BE_UPDATED))
//
                changeTasksLocally(task = task.toTaskForUi().copy(
                    syncState = SYNC_STATE.TO_BE_UPDATED
                ))
                Log.d("tag1" , "Updated task is $task")
                webSocketsRepo.sendTask(
                    WebsocketMessage(
                        type = "update_task", task = taskDTO
                    )
                )
            }catch (e : Exception){
                Log.d("tag1" , e.message.toString())
                onError(e.message.toString())
            }
        }
    }
    fun retryTask(task : TaskEntity , taskCompletion : TaskCompletion? = null){
        Log.d("tag24" , "task is $task")
        if(task.syncState !in listOf(SYNC_STATE.TO_ADD_TASK_COMPLETION , SYNC_STATE.TO_DELETE_TASK_COMPLETION)) {

            viewModelScope.launch(Dispatchers.IO) {
                val locked = mainRepo.retryTask(
                    task = task
                )
                if (locked) {
//                    changeTasksLocally(
//                        task.toTaskForUi().copy(
//                            syncState = SYNC_STATE.SYNCING
//                        )
//                    )
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
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                Log.d("tag24" , "Sending task completion $taskCompletion")
               webSocketsRepo.sendTask(
                   WebsocketMessage(
                      type = "task_completion" , taskCompletion = taskCompletion!!.toTaskCompletionDto()
                   )
               )
            }
        }
    }
    fun addTag(tag : String){
        if(tag !in tags) {
            tags.add(tag)
        }
    }
}

