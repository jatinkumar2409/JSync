package com.example.jsync.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.usecases.AddTaskUseCase
import com.example.jsync.domain.tasks.usecases.LoadTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val networkObserver: NetworkObserver ,
    private val addTaskUseCase: AddTaskUseCase , private val loadTasksUseCase: LoadTasksUseCase
) : ViewModel() {
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus = _networkStatus.asStateFlow()
    private val _tasks = MutableStateFlow<List<TaskDTO>>(emptyList())
    val tasks = _tasks.asStateFlow()
   init {
       observeNetwork()
   }
    fun observeNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            networkObserver.observeNetwork().collect { it ->
                _networkStatus.value = it
            }
        }
    }
    fun addTask(taskDTO: TaskDTO){
        viewModelScope.launch(Dispatchers.IO) {
            addTaskUseCase.addTask(taskDTO)
        }
    }
    fun getTasks(onError : (String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = loadTasksUseCase.loadTasks()
            tasks.onSuccess { it ->
                _tasks.value = it
            }
            tasks.onFailure { it ->
                onError(it.message.toString())
            }
        }
    }
}