package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.room.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun addTask(task : TaskEntity) : TaskEntity

    suspend fun loadTasksFromServer(toBeDeleted : Set<String> , onError : (String) -> Unit)
     fun getDisplayableTasks(userId : String) : Flow<List<TaskEntity>>
    suspend fun updateTask(task : TaskEntity) : TaskEntity
    suspend fun deleteTask(task : TaskEntity)

    suspend fun retryTask(task : TaskEntity) : Boolean
    suspend fun upsertSyncedTask(task : TaskEntity)

    suspend fun deleteSyncedTask(task : TaskEntity)

     fun getTasksOfDate(belongsToDate : Long , userId: String) : Flow<List<TaskEntity>>

    suspend fun getTaskCompletionsByDateFromRoom(date : Long , onError: (String) -> Unit)
}
