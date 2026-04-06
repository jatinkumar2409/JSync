package com.example.jsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskCompletionDTO(
    val id : String ,
    val taskId : String ,
    val completionDate : Long ,
    val isDeleted : Boolean = false
)
