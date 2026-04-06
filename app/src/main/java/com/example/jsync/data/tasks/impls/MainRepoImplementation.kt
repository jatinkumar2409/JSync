package com.example.jsync.data.tasks.impls

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jsync.core.helpers.SyncSchedular
import com.example.jsync.core.helpers.SyncWorkerForTasks
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.core.helpers.toTaskCompletion
import com.example.jsync.core.helpers.toTaskEntity
import com.example.jsync.data.room.Daos.TaskCompletionDao
import com.example.jsync.data.room.Daos.TaskDao
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskEntity
import com.example.jsync.domain.tasks.repos.MainRepository
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import com.example.jsync.domain.tasks.repos.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainRepoImplementation(
    private val dao : TaskDao ,
    private val taskCompletionDao: TaskCompletionDao ,
    private val context : Context ,
    private val repo : TaskRepository ,
    private val taskCompletionRepo : TaskCompletionRepo ,
    private val prefDatastore : prefDatastore ,
    private val syncSchedular: SyncSchedular
) : MainRepository {
    override suspend fun addTask(task: TaskEntity): TaskEntity {
        val userId = prefDatastore.userId.first() ?: ""
        val newTask =  task.copy(
            userId = userId ,
            syncState = SYNC_STATE.TO_BE_CREATED ,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertTask(
            newTask
        )
//        enqueueSync()
        return newTask
    }

    override suspend fun loadTasksFromServer(toBeDeleted : Set<String> , onError : (String) -> Unit) {
        Log.d("tag1" , "load tasks from server is calling")
        val tasks = repo.getTasks()
        tasks.onSuccess { it ->
            Log.d("tag1" , "task is ${it.joinToString( " , ")}")
            it.forEach { task ->
                if (task.isDeleted) {
                    Log.d("tag1" , "deleting task with ${task.taskName}")
                    if(task.id in toBeDeleted) prefDatastore.removeToBeDeleted(task.id)
                   dao.deleteTask(task.toTaskEntity())
                } else {
                    if(task.id !in toBeDeleted){
                        dao.upsertTask(task.toTaskEntity())
                        Log.d("tag1" , "upserting the task")
                    }
                }
            }
        }.onFailure {
            Log.d("tag" , "Error while loading tasks" +  it.message.toString())
            onError(it.message.toString())
        }

    }

    override fun getDisplayableTasks(userId : String): Flow<List<TaskEntity>> {
        return dao.getDisplayableTasks(userId)
    }

    override suspend fun updateTask(task: TaskEntity): TaskEntity {
        val userId = prefDatastore.userId.first() ?: ""
    val newState = if(task.syncState == SYNC_STATE.TO_BE_CREATED) SYNC_STATE.TO_BE_CREATED else SYNC_STATE.TO_BE_UPDATED
    val newTask = task.copy(
        userId = userId,
        syncState = newState ,
        updatedAt = System.currentTimeMillis()
    )
        dao.upsertTask(
           newTask
        )
//        enqueueSync()
        return newTask
    }

    override suspend fun deleteTask(task: TaskEntity) {
        val userId = prefDatastore.userId.first() ?: ""
        if(task.syncState == SYNC_STATE.TO_BE_CREATED){
            dao.deleteTask(task)
        }
        else{
            dao.upsertTask(
                task.copy(
                    userId = userId,
                    syncState = SYNC_STATE.TO_BE_DELETED , updatedAt = System.currentTimeMillis()
                )
            )
            prefDatastore.saveUserId(task.id)
            syncSchedular.enqueueSync()
        }
    }

    override suspend fun retryTask(task: TaskEntity) : Boolean {
        val locked = dao.updateStateIfUnchanged(
            id = task.id, userId = task.userId , fromState = task.syncState
        )
        return locked == 1
    }

    override suspend fun upsertSyncedTask(task: TaskEntity) {
        try{
            dao.upsertTask(
                task = task.copy(
                    syncState = SYNC_STATE.SYNCED
                )
            )
        }
        catch (e : Exception){
         Log.d("tag2" , "exception in upserting is ${e.message}")
        }
    }

    override suspend fun deleteSyncedTask(task: TaskEntity) {
        dao.deleteTask(task)
    }

    override fun getTasksOfDate(belongsToDate: Long, userId: String) : Flow<List<TaskEntity>> {
       return dao.getTasksByDate(
           userId = userId , startOfDay = timeHelper.getStartOfDay(belongsToDate) , endOfDay = timeHelper.getEndOfDay(belongsToDate)
       )
    }

    override suspend fun getTaskCompletionsByDateFromRoom(
        date: Long,
        onError: (String) -> Unit
    ) {
    }


}