package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.models.TaskDTO

interface TaskRepository {
    suspend fun addTask(taskDTO: TaskDTO) : Result<Boolean>

}