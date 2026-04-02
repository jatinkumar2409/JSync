package com.example.jsync.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jsync.data.room.Daos.TaskDao
import com.example.jsync.data.room.converters.SyncConverters
import com.example.jsync.data.room.entities.TaskEntity

@Database(entities = [TaskEntity::class] , version = 1)
@TypeConverters(SyncConverters::class)
abstract class JSyncDatabase : RoomDatabase(){
    abstract fun taskDao() : TaskDao
}