package com.example.jsync.domain.websockets.repo

import com.example.jsync.data.models.TaskDTO
import kotlinx.coroutines.flow.Flow

interface WebSocketsRepo {
    val messages : Flow<TaskDTO>
    suspend fun connect(userId : String)
    suspend fun sendTask(taskDTO: TaskDTO)
    suspend fun disconnect()
}