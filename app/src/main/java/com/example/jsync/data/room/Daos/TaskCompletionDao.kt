package com.example.jsync.data.room.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskCompletion
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskCompletionDao {
    @Upsert
    suspend fun upsertTaskCompletion(taskCompletion : TaskCompletion)

    @Delete
    suspend fun deleteTaskCompletion(taskCompletion : TaskCompletion)

    @Query("""
        SELECT taskCompletions.* FROM taskCompletions INNER JOIN tasks ON taskCompletions.taskId = tasks.id
        WHERE tasks.userId = :userId AND
               taskCompletions.completionDate BETWEEN :startDate AND :endDate
    """)
    fun getTaskCompletionOfDate(startDate : Long , endDate : Long , userId : String) : Flow<List<TaskCompletion>>

    @Query("""
        SELECT * FROM taskCompletions WHERE syncState != 'SYNCED'
    """)
    suspend fun getPendingTaskCompletions() : List<TaskCompletion>

    @Query(
        """
            UPDATE taskCompletions 
            SET syncState = :syncState 
            WHERE id = :id and syncState = :expectedState
        """
    )
    suspend fun updateStateIfUnchanged(
        id: String,
        expectedState: SYNC_STATE,
        syncState: SYNC_STATE) : Int

    @Query("SELECT * FROM taskCompletions")
    fun getAllTaskCompletions() : Flow<List<TaskCompletion>>

}