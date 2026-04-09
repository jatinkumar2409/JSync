package com.example.jsync.koin

import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.core.helpers.SyncSchedular
import com.example.jsync.core.helpers.SyncWorkerForTasks
import com.example.jsync.core.helpers.TokenAuthenticator
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val helperModule = module {
    single {
        manageToken(get())
    }
    single {
        NetworkObserver(get())
    }
    single {
        prefDatastore(get())
    }
    single {
        TokenAuthenticator(get())
    }
    worker {
        SyncWorkerForTasks(
            get() , get() , get() , get() , get() , get() , get() , get()
        )
    }
    single {
        TokenAuthenticator(get())
    }
    single{
        SyncSchedular(get())
    }
}