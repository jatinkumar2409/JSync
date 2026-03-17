package com.example.jsync.data.tasks.impls

import android.util.Log
import com.example.jsync.core.helpers.GetClient
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.repos.TaskRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first

class TaskRepoImplementation(private val manageToken : manageToken , private val prefDatastore: prefDatastore) : TaskRepository {
    private val client = GetClient.getClient(connectionTimeout = 15_000L , requestTimeout = 12_000L , socketTimeout = 12_000L)
    override suspend fun addTask(taskDTO: TaskDTO): Result<Boolean> {
        val userId = prefDatastore.userId.first() ?: ""
        if (userId.trim().isEmpty()) return Result.failure(Exception("User Id is empty"))

        try {
            Log.d("tag" , "add task is running")
            val newTask = taskDTO.copy(userId = userId)
            val token = manageToken.getAccessToken() ?: ""
            val response = client.post("/add_task"){
                setBody(newTask)
                contentType(ContentType.Application.Json)
                headers{
                    append("Authorization" , "Bearer $token")
                }
            }
            Log.d("TAG" , "addTask: ${response.status} ")
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
        val userId = prefDatastore.userId.first() ?: ""
        if (userId.trim().isEmpty()) return Result.failure(Exception("User Id is empty"))
        try{
            val token = manageToken.getAccessToken() ?: ""
            val response = client.get("/get_tasks?user_id=$userId"){
                headers {
                    append("Authorization" , "Bearer $token")
                }
            }
            if (response.status.isSuccess()){
                return Result.success(response.body())
            }
            else{
                val error = response.body<ErrorResponse>()
                return Result.failure(Exception(error.detail))
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }
}