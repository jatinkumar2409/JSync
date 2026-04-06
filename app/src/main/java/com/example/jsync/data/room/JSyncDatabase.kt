package com.example.jsync.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jsync.data.room.Daos.TaskCompletionDao
import com.example.jsync.data.room.Daos.TaskDao
import com.example.jsync.data.room.converters.SyncConverters
import com.example.jsync.data.room.entities.TaskCompletion
import com.example.jsync.data.room.entities.TaskEntity

@Database(entities = [TaskEntity::class, TaskCompletion::class] , version = 2)
@TypeConverters(SyncConverters::class)
abstract class JSyncDatabase : RoomDatabase(){
    abstract fun taskDao() : TaskDao

    abstract fun taskCompletionDao() : TaskCompletionDao
}