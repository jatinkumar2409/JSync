package com.example.jsync.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = false)
    val id : String ,
    val taskName : String ,
    val userId : String ,
    val dueTo : Long? ,
    val type : Int ,
    val priority : Int ,
    val hasDone : Boolean ,
    val tags : String ,
    val isDeleted : Boolean ,
    val updatedAt : Long
)


