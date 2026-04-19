package com.example.jsync.data.tasks.impls

import android.util.Log
import com.example.jsync.core.helpers.GetClient
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.core.helpers.timeHelper
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.data.models.TaskCompletionDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.repos.TaskRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first

class TaskRepoImplementation(private val manageToken : manageToken) : TaskRepository {
    private val client = GetClient.getClient(connectionTimeout = 30_000L , requestTimeout = 30_000L , socketTimeout = 60_000L)
    override suspend fun addTask(taskDTO: TaskDTO): Result<Boolean> {
        val token = manageToken.getAccessToken() ?: ""
        if (token.trim().isEmpty()) return Result.failure(Exception("User Id is empty"))
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.post("/add_task"){
                header("Authorization" , "Bearer $token")
                setBody(taskDTO)
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                return Result.success(true)
            }
            else{
                val error = response.body<ErrorResponse>()
                Log.d("tag" , "error $error")
                return Result.failure(Exception(error.detail))
            }
        }
        catch (e : Exception){
            Log.d("tag" , "error ${e.message}")
            return Result.failure(e)
        }

    }

    override suspend fun getTasks(): Result<List<TaskDTO>> {
        val token = manageToken.getAccessToken() ?: ""
        if (token.trim().isEmpty()) return Result.failure(Exception("Token is empty"))
        try{
            val token = manageToken.getAccessToken() ?: ""
            Log.d("tag" , "Access token is $token")
            val response = client.get("/get_tasks"){
                header("Authorization", "Bearer $token")
            }
                Log.d("tag1",  "Status is success")
                return Result.success(response.body())
        }
        catch (e : Exception){
            throw e
        }
    }

    override suspend fun updateTask(task: TaskDTO): Result<Boolean> {
        try{
            val token = manageToken.getAccessToken() ?: ""
            val response = client.put("/update_task"){
                header("Authorization" , "Bearer $token")
                setBody(task)
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

    override suspend fun deleteTask(taskId: String): Result<Boolean> {
       try {
           val token = manageToken.getAccessToken() ?: ""
           val response = client.delete("/delete_task?task_id=$taskId"){
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
           Log.d("worker" , e.message.toString())
           return Result.failure(e)
       }
    }


}