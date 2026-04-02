package com.example.jsync.data.models

import com.example.jsync.data.room.entities.SYNC_STATE
import kotlinx.serialization.Serializable

@Serializable
data class TaskForUi(
    val id : String ,
    val taskName : String ,
    val dueAt : Long? = null ,
    val type : Int ,
    val priority : Int ,
    val hasDone : Boolean ,
    val tags : String ,
    val syncState : SYNC_STATE = SYNC_STATE.SYNCED
)
