package com.example.jsync.data.models

import kotlinx.serialization.Serializable
import okhttp3.WebSocket

@Serializable
data class AiRequestDTO(
    val tasks : List<TaskDTO> ,
    val message : String
)
