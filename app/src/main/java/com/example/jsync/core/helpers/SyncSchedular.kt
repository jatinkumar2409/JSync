package com.example.jsync.core.helpers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SyncSchedular(private val context : Context) {
    fun enqueueSync(){
        val request = OneTimeWorkRequestBuilder<SyncWorkerForTasks>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    ).build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL , 10 , TimeUnit.SECONDS
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "sync_tasks" , ExistingWorkPolicy.KEEP , request
        )
    }
}