package com.example.jsync.data.websockets.impls

import android.util.Log
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class WebsocketsImpl() : WebSocketsRepo {
    private val client = HttpClient {
        install(WebSockets)
    }
    private var session : DefaultClientWebSocketSession? = null
    private val _messages = MutableSharedFlow<TaskDTO>()
    override val messages = _messages
    override suspend fun connect(userId: String) {
        session = client.webSocketSession(
            host = "192.168.254.241",
            port = 8000,
            path = "/ws"
        )
        session?.let{
            manageSocket()
        }
    }
    private fun manageSocket(){
        CoroutineScope(Dispatchers.IO).launch {
            for (frame in session!!.incoming){
                when(frame){
                  is  Frame.Text -> {
                      Log.d("tag" , frame.readText())
                  }

                    else -> {}
                }
            }
        }
    }

    override suspend fun sendTask(taskDTO: TaskDTO) {
        session?.send(Frame.Text(taskDTO.toString()))
    }

    override suspend fun disconnect() {
        session?.close()
    }
}