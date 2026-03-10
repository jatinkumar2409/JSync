package com.example.jsync.data.models

data class TaskDTO(
    val id : String ,
    val taskName : String ,
    val dueAt : Long ,
    val type : Int ,
    val priority : Int ,
    val hasDone : Boolean ,
    val tags : String ,
    val isDeleted : Boolean,
    val updatedAt : Long
)
