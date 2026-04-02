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
        Log.d("tag" , "Work manager is running ...")
        val userId = prefDatastore.userId.first() ?: ""
        if (userId.trim().isEmpty()) return Result.failure()
        return try {
            val pendingTasks = dao.getPendingTasks(userId = userId)
            var shouldRetry = false
            for (task in pendingTasks){
                val locked = dao.updateStateIfUnchanged(
                    id = task.id, userId = userId , fromState = task.syncState
                )
                if(locked == 0) continue
                try {
                  when(task.syncState){
                      SYNC_STATE.TO_BE_CREATED -> {
                          repo.addTask(
                              task.toTaskDto()
                          )
                          dao.markSyncedIfUnchanged(id = task.id  , userId = userId, expectedUpdatedAt = task.updatedAt)
                      }
                      SYNC_STATE.TO_BE_UPDATED -> {
                           repo.updateTask(
                               task.toTaskDto()
                           )
                          dao.markSyncedIfUnchanged(id = task.id  , userId = userId, expectedUpdatedAt = task.updatedAt)
                      }
                      SYNC_STATE.TO_BE_DELETED -> {
                          repo.deleteTask(
                              task.id
                          )
                      }
                      else -> {}
                  }
                }
                catch(e : IOException){
                    shouldRetry = true
                }
                catch (e : ClientRequestException){
                    if(e.response.status.value == 500){
                        shouldRetry = true
                    }
                    if(e.response.status.value == 401){
                       val token =  tokenAuthenticator.rotateAccessToken()
                        if(token.trim().isEmpty()) return Result.failure();
                        return Result.retry()
                    }
                }
                catch (e : Exception){
                    shouldRetry = true
                }
            }
            val stillPending = dao.getPendingTasks(userId = userId)
            return when{
                shouldRetry -> Result.retry()
                stillPending.isNotEmpty() -> Result.retry()
                else -> Result.success()
            }
        }
        catch (e : IOException){
            Result.retry()
        }
        catch (e : Exception){
            Result.retry()
        }
    }

}