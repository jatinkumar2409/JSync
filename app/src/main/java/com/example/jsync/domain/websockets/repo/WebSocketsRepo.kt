package com.example.jsync.domain.websockets.repo

import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.WebsocketMessage
import com.example.jsync.data.websockets.impls.WebsocketState
import kotlinx.coroutines.flow.Flow

interface WebSocketsRepo {
    val messages : Flow<WebsocketMessage>
    val websocketState : Flow<WebsocketState>
    suspend fun connect(userId : String , onError : (String) -> Unit)
    suspend fun sendTask(message : WebsocketMessage)
    suspend fun disconnect()
}