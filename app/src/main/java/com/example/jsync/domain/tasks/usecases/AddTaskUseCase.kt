package com.example.jsync.domain.tasks.usecases

import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.repos.TaskRepository

class AddTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun addTask(taskDTO: TaskDTO): Result<Boolean> {
        return taskRepository.addTask(taskDTO)
    }

}