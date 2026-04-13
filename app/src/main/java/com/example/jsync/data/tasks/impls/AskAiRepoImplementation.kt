package com.example.jsync.data.tasks.impls

import com.example.jsync.core.helpers.GetClient
import com.example.jsync.data.models.AiRequestDTO
import com.example.jsync.data.models.AiResponseDTO
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.domain.tasks.repos.AskAiRepository
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AskAiRepoImplementation() : AskAiRepository {
    private val client = GetClient.getClient(connectionTimeout = 30_000L , requestTimeout = 30_000L , socketTimeout = 60_000L)
    override suspend fun askAi(aiRequest: AiRequestDTO): Result<AiResponseDTO> {
        try {
            val response = client.post("/ask_ai"){
                contentType(ContentType.Application.Json)
                setBody(aiRequest)
            }
            if (response.status.isSuccess()){
                return Result.success(response.body<AiResponseDTO>())
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
}