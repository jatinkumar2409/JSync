package com.example.jsync.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taskCompletionStatus")
data class TaskCompletionStatus(
    @PrimaryKey(autoGenerate = false)
    val taskId : String ,
    val completionDate : Long
)
