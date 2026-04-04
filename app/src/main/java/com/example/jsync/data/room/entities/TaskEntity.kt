package com.example.jsync.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = false)
    val id : String ,
    val taskName : String ,
    val userId : String ,
    val dueAt : Long? ,
    val type : Int ,
    val priority : Int ,
    val hasDone : Boolean ,
    val tags : String ,
    val updatedAt : Long ,
    val belongsToDate : Long = System.currentTimeMillis() ,
    val expiryTime : Long? = null ,
    val syncState : SYNC_STATE = SYNC_STATE.UNSYNCED
)

enum class SYNC_STATE{
    TO_BE_CREATED ,
    TO_BE_UPDATED ,
    TO_BE_DELETED ,
    SYNCED , UNSYNCED , SYNCING , FAILED , FAILED_DELETE
}


