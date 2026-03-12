package com.example.jsync.domain.tasks.usecases

import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.auth.repos.AuthRepository
import com.example.jsync.domain.tasks.repos.TaskRepository

class LoadTasksUseCase(private val taskRepo : TaskRepository) {
    suspend fun loadTasks()  = taskRepo.getTasks()
}