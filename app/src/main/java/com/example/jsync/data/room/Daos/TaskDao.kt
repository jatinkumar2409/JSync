package com.example.jsync.data.room.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.internal.concurrent.Task


@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task : TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAllTasks() : Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND syncState != 'TO_BE_DELETED' ORDER BY priority DESC")
    fun getDisplayableTasks(userId: String) : Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND syncState != 'SYNCED' ORDER BY updatedAt ASC")
   suspend fun getPendingTasks(userId: String) : List<TaskEntity>

    @Query("""
        UPDATE tasks 
        SET syncState = 'SYNCED'
        WHERE id = :id AND userId = :userId AND updatedAt = :expectedUpdatedAt 
    """)
    suspend fun markSyncedIfUnchanged(
        id: String,
        userId: String ,
        expectedUpdatedAt: Long
    )

    @Query("""
        UPDATE tasks
        SET syncState = :syncState
        WHERE id = :id AND userId = :userId AND syncState = :fromState
        """)
    suspend fun updateStateIfUnchanged(
        id: String,
        userId: String,
        fromState: SYNC_STATE,
        syncState: SYNC_STATE = SYNC_STATE.SYNCING
    ) : Int

    @Query("""
        SELECT * FROM tasks WHERE userId = :userId AND syncState != 'TO_BE_DELETED' 
 AND (
  (
    type = 1 AND belongsToDate BETWEEN :startOfDay AND :endOfDay
  ) OR (
    type = 0 AND belongsToDate BETWEEN :startOfDay AND :endOfDay AND :currentTime < expiryTime
  ) OR (
      type = 2 AND belongsToDate <= :endOfDay 
  )
 )
    """)
    fun getTasksByDate(
        userId: String,
        startOfDay: Long,
        endOfDay: Long ,
        currentTime : Long = System.currentTimeMillis()
    ): Flow<List<TaskEntity>>

}