package com.example.jsync.data.websockets.impls

import android.util.Log
import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.core.helpers.TokenAuthenticator
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.WebsocketMessage
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher

class WebsocketsImpl(private val manageToken: manageToken ,
    private val networkObserver: NetworkObserver , private val tokenAuthenticator: TokenAuthenticator) : WebSocketsRepo {
   private val client = HttpClient(io.ktor.client.engine.okhttp.OkHttp){
    install(WebSockets)
   }

    private var session : DefaultClientWebSocketSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val webscoketState_ = MutableStateFlow(WebsocketState.DISCONNECTED)
    override val websocketState = webscoketState_.asStateFlow()
    private val _messages = MutableSharedFlow<WebsocketMessage>()
    override val messages = _messages
    private var lastSeenAt = 0L
    private var reconnectJob : Job? = null

    private var connectionJob : Job? = null

    override suspend fun connect(userId : String , onError: (String) -> Unit) {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            Log.d("tag" , "Connecting to websocket ....")
                networkObserver.observeNetwork().collectLatest { networkStatus ->
                    if(networkStatus){
                        connectionJob?.cancel()
                        connectionJob =  launch {
                            manageConnection(userId ,onError)
                        }
                    }
                    else{
                        connectionJob?.cancel()
                        session?.close()
                        markDisconnected()
                    }
                }
        }
    }
    private suspend fun manageConnection(userId : String , onError : (String) -> Unit){
        while (currentCoroutineContext().isActive){
            try {
                webscoketState_.value = WebsocketState.CONNECTING
                val token = manageToken.getAccessToken()
                Log.d("ws" , "running loop with token $token")
                if(token == null){
                    delay(2000)
                    continue
                }
                session?.close()
                session = client.webSocketSession{
                    url {
                        protocol = URLProtocol.WS
                        host = "192.168.230.241"
                        port = 8000
                        encodedPath = "/ws/tasks"
                        header("Authorization" , "Bearer $token")
                        parameter("token" , token)
                        parameter("userId" , userId)

                    }
                }
                webscoketState_.value = WebsocketState.CONNECTED
                lastSeenAt = now()
                coroutineScope {
                    val reader = launch { readLoop(onError)}
                    val heartBeatMonitor = launch { heartBeatMonitorLoop() }
                }

            }
            catch (e : Exception){
                markDisconnected()
                delay(3000)
            }
        }
    }
    private suspend fun readLoop(onError: (String) -> Unit){
        try {
            for (frame in session!!.incoming){
                when(frame){
                    is  Frame.Text -> {
                        lastSeenAt = now()
                        val text = frame.readText()
                        val message = Json.decodeFromString<WebsocketMessage>(text)
                        if(message.type == "pong") continue
                        Log.d("websocket" , "Receiving $message")
                        _messages.emit(
                            message
                        )
                        if(message.type == "error"){
                            if (message.error == "TOKEN_EXPIRED"){
                                tokenAuthenticator.rotateAccessToken()
                                currentCoroutineContext().cancel()
                                return
                            }
                            onError(
                                message.error!!
                            )
                        }
                    }
                    is Frame.Close -> {
                        val reason = frame.readReason()
                        if(reason?.code == 1008.toShort()){
                            tokenAuthenticator.rotateAccessToken()
                            currentCoroutineContext().cancel()
                            return
                        }
                    }
                    else -> {}
                }
            }
        }
        catch (e : Exception){
            val reason = session?.closeReason?.await()
            Log.d("tag" , "close reason is ${reason?.code}")
            if(reason?.code == 1008.toShort()){
                tokenAuthenticator.rotateAccessToken()
                currentCoroutineContext().cancel()
            }
            else {
                throw e
            }
        }
    }

    private suspend fun heartBeatMonitorLoop(){
        while (currentCoroutineContext().isActive){
            delay(25_000)
            val diff = now() - lastSeenAt
            when{
                diff < 30_000 -> {
                    webscoketState_.value = WebsocketState.CONNECTED
                }
                diff in 30_000..60_000 -> {
                    webscoketState_.value = WebsocketState.STALE
                }
                else ->{
                    throw Exception("Disconnected")
                }
            }
            try {
               session?.send(Frame.Text(Json.encodeToString(WebsocketMessage(type = "ping" , task = null))))
            }
            catch (e : Exception){
                throw e
            }
        }
    }

    override suspend fun sendTask(message : WebsocketMessage) {
        Log.d("websocket" , "Sending message $message")
        val json = Json.encodeToString(message)
        try {
            if(session == null) return
            if(websocketState.value == WebsocketState.DISCONNECTED) return
            if(websocketState.value == WebsocketState.CONNECTING) delay(2000);
                session?.send(Frame.Text(json))

        }catch (e : Exception){
            markDisconnected()
            throw e
        }
    }

    override suspend fun disconnect() {
        reconnectJob?.cancel()
        session?.close()
        markDisconnected()
    }
    private fun markDisconnected(){
        session = null
        webscoketState_.value = WebsocketState.DISCONNECTED
    }
    private fun now() = System.currentTimeMillis()
}

enum class WebsocketState{
    CONNECTING , CONNECTED , DISCONNECTED , STALE
}