package com.example.jsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketMessage(
    val type : String ,
    val task : TaskDTO? ,
    val error : String? = null
)
