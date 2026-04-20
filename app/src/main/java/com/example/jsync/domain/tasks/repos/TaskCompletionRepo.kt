package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import kotlinx.coroutines.flow.Flow

interface TaskCompletionRepo {
    suspend fun addTaskCompletionToServer(taskCompletion: TaskCompletionDTO): Result<Boolean>
    suspend fun updateTaskCompletionToServer(taskCompletion: TaskCompletionDTO): Result<Boolean>
    suspend fun updateTaskCompletionStateIfUnchanged(fromState : SYNC_STATE , toState : SYNC_STATE , id : String) : Int
    suspend fun deleteTaskCompletionToServer(id: String): Result<Boolean>
    suspend fun loadTasksCompletion(): Result<List<TaskCompletionDTO>>
    suspend fun loadTaskCompletionOfDateFromServer(toBeDeleted : Set<String>  ,date: Long , onError : (String) -> Unit)

    suspend fun getTaskCompletionsOfDate(date : Long , userId : String) : Flow<List<TaskCompletion>>

    suspend fun getAllTaskCompletions() : Flow<List<TaskCompletion>>

    suspend fun upsertSyncedTaskCompletion(taskCompletion : TaskCompletion)
    suspend fun deleteSyncedTaskCompletion(taskCompletion : TaskCompletion)

    suspend fun addTaskCompletion(taskCompletion: TaskCompletion)
    suspend fun deleteTaskCompletion(taskCompletion: TaskCompletion)

}