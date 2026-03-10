package com.example.jsync.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurringTasks")
data class RecurringTask(
    @PrimaryKey(autoGenerate = false)
    val taskId : String ,
    val taskName : String ,
    val dueAt : Long ,
    val priority : Int ,
    val tags : String
)
