package com.example.jsync.data.tasks.impls

import com.example.jsync.core.helpers.GetClient
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.repos.TaskRepository
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess

class TaskRepoImplementation(private val manageToken : manageToken) : TaskRepository {
    private val client = GetClient.getClient(connectionTimeout = 15_000L , requestTimeout = 12_000L , socketTimeout = 12_000L)

    override suspend fun addTask(taskDTO: TaskDTO): Result<Boolean> {
        try {
            val token = manageToken.getAccessToken() ?: ""
            val response = client.post("/add_task"){
                setBody(taskDTO)
                contentType(ContentType.Application.Json)
                headers{
                    append("Authorization" , "Bearer $token")
                }
            }
            if (response.status.isSuccess()) {
                return Result.success(true)
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