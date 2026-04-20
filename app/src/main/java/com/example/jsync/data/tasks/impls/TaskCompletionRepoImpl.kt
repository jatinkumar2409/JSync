package com.example.jsync.data.tasks.impls

import android.util.Log
import com.example.jsync.core.helpers.GetClient
import com.example.jsync.core.helpers.RetryRequest
import com.example.jsync.core.helpers.SyncSchedular
import com.example.jsync.core.helpers.TokenAuthenticator
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.core.helpers.toTaskCompletion
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.room.Daos.TaskCompletionDao
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow

class TaskCompletionRepoImpl(
    private val taskCompletionDao : TaskCompletionDao ,
    private val manageToken : manageToken ,
    private val syncSchedular: SyncSchedular ,
    private val tokenAuthenticator: TokenAuthenticator ,
    private val prefDatastore: prefDatastore
) : TaskCompletionRepo {
    private val client = GetClient.getClient(connectionTimeout = 30_000L , requestTimeout = 30_000L , socketTimeout = 60_000L)

    override suspend fun addTaskCompletionToServer(taskCompletion: TaskCompletionDTO): Result<Boolean> {
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.post("/add_task_completion"){
                header("Authorization" , "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(taskCompletion)
            }
            if(response.status.isSuccess()){
                return Result.success(true)
            }
            else{
                val errorBody = response.body<ErrorResponse>()
                return Result.failure(Exception(errorBody.detail))
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }
    override suspend fun updateTaskCompletionToServer(taskCompletion: TaskCompletionDTO): Result<Boolean> {
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.put("/update_task_completion"){
                header("Authorization" , "Bearer $token")
                setBody(taskCompletion)
                contentType(ContentType.Application.Json)
            }
            if(response.status.isSuccess()){
                return Result.success(true)
            }
            else{
                val errorBody = response.body<ErrorResponse>()
                return Result.failure(Exception(errorBody.detail))
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }

    override suspend fun updateTaskCompletionStateIfUnchanged(
        fromState: SYNC_STATE,
        toState: SYNC_STATE,
        id: String
    ): Int {
        return taskCompletionDao.updateStateIfUnchanged(
            id = id , expectedState = fromState , syncState = toState
        )
    }

    override suspend fun deleteTaskCompletionToServer(id: String): Result<Boolean> {
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.delete("/delete_task_completion?id=$id"){
                header("Authorization" , "Bearer $token")
            }
            if(response.status.isSuccess()){
                return Result.success(true)

            }
            else{
                val errorBody = response.body<ErrorResponse>()
                return Result.failure(Exception(errorBody.detail))
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }

    override suspend fun loadTasksCompletion(): Result<List<TaskCompletionDTO>> {
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.get("/load_tasks_completions"){
                header("Authorization" , "Bearer $token")
            }
            if (response.status.isSuccess()){
                return Result.success(response.body<List<TaskCompletionDTO>>())
            }
            else{
                val errorBody = response.body<ErrorResponse>()
                return Result.failure(Exception(errorBody.detail))
            }

        }catch (e : Exception){
            return Result.failure(e)
        }
    }

    override suspend fun loadTaskCompletionOfDateFromServer(toBeDeleted : Set<String> ,date: Long , onError : (String) -> Unit) {
        try {
            Log.d("tag1" , "load tasks completions is being called")
            val startOfDay = timeHelper.getStartOfDay(date)
            val endOfDay = timeHelper.getEndOfDay(date)
            val taskCompletions = RetryRequest.callWithRetry(authenticator = tokenAuthenticator){
                val token = manageToken.getAccessToken() ?: ""
               val response =  client.get("/load_task_completions?start_of_day=$startOfDay&end_of_day=$endOfDay"){
                header("Authorization" , "Bearer $token")
            }
                response.body<List<TaskCompletionDTO>>()
                }
             taskCompletions.forEach { taskCompletionDTO ->
                 if (taskCompletionDTO.isDeleted){
                     taskCompletionDao.deleteTaskCompletion(taskCompletionDTO.toTaskCompletion())
                 }
                 else{
                     if (taskCompletionDTO.id !in toBeDeleted){
                         taskCompletionDao.upsertTaskCompletion(taskCompletionDTO.toTaskCompletion())
                     }
                 }
             }

        }
        catch (e : Exception){
            Log.d("tag13" , "Error is ${e.message}")
            onError(e.message.toString())
        }
    }

    override suspend fun getTaskCompletionsOfDate(date: Long , userId : String): Flow<List<TaskCompletion>> {
        return taskCompletionDao.getTaskCompletionOfDate(
            startDate = timeHelper.getStartOfDay(date) , endDate = timeHelper.getEndOfDay(date) , userId = userId
        )
    }

    override suspend fun getAllTaskCompletions(): Flow<List<TaskCompletion>> {
        return taskCompletionDao.getAllTaskCompletions()
    }

    override suspend fun upsertSyncedTaskCompletion(taskCompletion: TaskCompletion) {
        taskCompletionDao.upsertTaskCompletion(taskCompletion)
    }

    override suspend fun deleteSyncedTaskCompletion(taskCompletion: TaskCompletion) {
            taskCompletionDao.deleteTaskCompletion(taskCompletion)
    }

    override suspend fun addTaskCompletion(
        taskCompletion: TaskCompletion
    ) {
        taskCompletionDao.upsertTaskCompletion(
            taskCompletion
        )
        syncSchedular.enqueueSync()
    }

    override suspend fun deleteTaskCompletion(
        taskCompletion: TaskCompletion
    ) {
        if (taskCompletion.syncState == SYNC_STATE.TO_BE_CREATED){
            taskCompletionDao.deleteTaskCompletion(taskCompletion)
        }
        else{
            taskCompletionDao.upsertTaskCompletion(
                taskCompletion.copy(
                    syncState = SYNC_STATE.TO_BE_DELETED
                )
            )
        }
        prefDatastore.saveToBeDeletedTaskCompletions(taskCompletion.id)
        syncSchedular.enqueueSync()
    }


}
