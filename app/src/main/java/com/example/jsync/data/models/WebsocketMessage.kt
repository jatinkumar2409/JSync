package com.example.jsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketMessage(
    val type : String ,
    val task : TaskDTO? = null ,
    val taskCompletion : TaskCompletionDTO? = null ,
    val error : String? = null
)
