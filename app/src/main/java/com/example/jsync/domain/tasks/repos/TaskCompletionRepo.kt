package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.room.entities.TaskCompletion
import kotlinx.coroutines.flow.Flow

interface TaskCompletionRepo {
    suspend fun addTaskCompletion(task: TaskDTO): Result<Boolean>
    suspend fun updateTaskCompletion(task: TaskDTO): Result<Boolean>
    suspend fun deleteTaskCompletion(id: String): Result<Boolean>
    suspend fun loadTasksCompletion(): Result<List<TaskDTO>>
    suspend fun loadTaskCompletionOfDateFromServer(date: Long): Result<List<TaskCompletionDTO>>

    suspend fun getTaskCompletionsOfDate(date : Long) : Flow<List<TaskCompletion>>

    suspend fun upsertSyncedTaskCompletion(taskCompletion : TaskCompletion)
    suspend fun deleteSyncedTaskCompletion(taskCompletion : TaskCompletion)


}