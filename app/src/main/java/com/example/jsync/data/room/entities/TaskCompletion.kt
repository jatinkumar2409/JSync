package com.example.jsync.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "taskCompletions")
data class TaskCompletion(
    @PrimaryKey(autoGenerate = false)
    val id : String,
    val taskId : String ,
    val completionDate : Long ,
    val syncState : SYNC_STATE
)
