package com.example.jsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    val id : String ,
    val taskName : String ,
    val userId : String ,
    val dueAt : Long? ,
    val type : Int ,
    val priority : Int ,
    val hasDone : Boolean ,
    val tags : String ,
    val updatedAt : Long
)
