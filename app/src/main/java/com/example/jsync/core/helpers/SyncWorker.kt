package com.example.jsync.core.helpers

import android.content.Context
import android.net.http.HttpException
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.room.Daos.TaskDao
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.domain.tasks.repos.TaskRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.first
import java.io.IOException

class SyncWorkerForTasks(
    context : Context, params : WorkerParameters , private val dao : TaskDao ,
    private val repo : TaskRepository , private val prefDatastore: prefDatastore , private val tokenAuthenticator: TokenAuthenticator
) : CoroutineWorker(appContext = context , params = params) {
    override suspend fun doWork(): Result {
        Log.d("WORKER", "---- SyncWorker START ----")

        val userId = prefDatastore.userId.first() ?: ""
        Log.d("WORKER", "UserId: '$userId'")

        if (userId.trim().isEmpty()) {
            Log.e("WORKER", "UserId is EMPTY → failing work")
            return Result.failure()
        }

        return try {
            val pendingTasks = dao.getPendingTasks(userId = userId)
            Log.d("WORKER", "Pending tasks count: ${pendingTasks.size}")

            var shouldRetry = false

            for (task in pendingTasks) {
                Log.d("WORKER", "Processing task: id=${task.id}, state=${task.syncState}")

                val locked = dao.updateStateIfUnchanged(
                    id = task.id,
                    userId = userId,
                    fromState = task.syncState
                )

                Log.d("WORKER", "Lock result for task ${task.id}: $locked")

                if (locked == 0) {
                    Log.d("WORKER", "Skipping task ${task.id} (state changed)")
                    continue
                }

                try {
                    when (task.syncState) {

                        SYNC_STATE.TO_BE_CREATED -> {
                            Log.d("WORKER", "Calling addTask for ${task.id}")
                            val result = repo.addTask(task.toTaskDto())
                            Log.d("WORKER", "addTask result: $result")

                            dao.markSyncedIfUnchanged(
                                id = task.id,
                                userId = userId,
                                expectedUpdatedAt = task.updatedAt
                            )
                            Log.d("WORKER", "Marked CREATED task synced: ${task.id}")
                        }

                        SYNC_STATE.TO_BE_UPDATED -> {
                            Log.d("WORKER", "Calling updateTask for ${task.id}")
                            val result = repo.updateTask(task.toTaskDto())
                            Log.d("WORKER", "updateTask result: $result")

                            dao.markSyncedIfUnchanged(
                                id = task.id,
                                userId = userId,
                                expectedUpdatedAt = task.updatedAt
                            )
                            Log.d("WORKER", "Marked UPDATED task synced: ${task.id}")
                        }

                        SYNC_STATE.TO_BE_DELETED -> {
                            Log.d("WORKER", "Calling deleteTask for ${task.id}")
                            val result = repo.deleteTask(task.id)
                            Log.d("WORKER", "deleteTask result: $result")
                            if(result.isSuccess){
                                dao.deleteTask(task)
                                prefDatastore.removeToBeDeleted(task.id)
                            }
                            else{
                                dao.upsertTask(
                                    task.copy(
                                        syncState = SYNC_STATE.FAILED_DELETE
                                    )
                                )
                            }
                        }

                        else -> {
                            Log.d("WORKER", "Unknown state for task ${task.id}")
                        }
                    }

                } catch (e: IOException) {
                    Log.e("WORKER", "IOException for task ${task.id}: ${e.message}", e)
                    shouldRetry = true
                }

                catch (e: ClientRequestException) {
                    val status = e.response.status.value
                    Log.e("WORKER", "ClientRequestException for task ${task.id}: status=$status, msg=${e.message}", e)

                    if (status == 500) {
                        Log.d("WORKER", "Server error (500) → will retry")
                        shouldRetry = true
                    }

                    if (status == 401) {
                        Log.d("WORKER", "Unauthorized (401) → rotating token")

                        val token = tokenAuthenticator.rotateAccessToken()
                        Log.d("WORKER", "New token after rotation: '$token'")

                        if (token.trim().isEmpty()) {
                            Log.e("WORKER", "Token rotation FAILED → failing work")
                            return Result.failure()
                        }

                        Log.d("WORKER", "Retrying work after token refresh")
                        return Result.retry()
                    }
                }

                catch (e: Exception) {
                    Log.e("WORKER", "Generic Exception for task ${task.id}: ${e.message}", e)
                    shouldRetry = true
                }
            }

            val stillPending = dao.getPendingTasks(userId = userId)
            Log.d("WORKER", "Still pending tasks after loop: ${stillPending.size}")
            Log.d("WORKER", "shouldRetry flag: $shouldRetry")

            val finalResult = when {
                shouldRetry -> {
                    Log.d("WORKER", "Final decision: RETRY (due to errors)")
                    Result.retry()
                }
                stillPending.isNotEmpty() -> {
                    Log.d("WORKER", "Final decision: RETRY (tasks still pending)")
                    Result.retry()
                }
                else -> {
                    Log.d("WORKER", "Final decision: SUCCESS")
                    Result.success()
                }
            }

            Log.d("WORKER", "---- SyncWorker END ----")
            finalResult

        } catch (e: IOException) {
            Log.e("WORKER", "Global IOException: ${e.message}", e)
            Result.retry()
        }

        catch (e: Exception) {
            Log.e("WORKER", "Global Exception: ${e.message}", e)
            Result.retry()
        }
    }

}