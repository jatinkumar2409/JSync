package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO

interface TaskRepository {
    suspend fun addTask(taskDTO: TaskDTO) : Result<Boolean>
    suspend fun getTasks() : Result<List<TaskDTO>>

    suspend fun updateTask(task : TaskDTO) : Result<Boolean>

    suspend fun  deleteTask(taskId : String) : Result<Boolean>

}