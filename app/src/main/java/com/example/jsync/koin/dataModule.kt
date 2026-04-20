package com.example.jsync.koin

import androidx.room.Room
import com.example.jsync.data.auth.impls.AuthRepoImplementation
import com.example.jsync.data.room.JSyncDatabase
import com.example.jsync.data.tasks.impls.AskAiRepoImplementation
import com.example.jsync.data.tasks.impls.MainRepoImplementation
import com.example.jsync.data.tasks.impls.TaskCompletionRepoImpl
import com.example.jsync.data.tasks.impls.TaskRepoImplementation
import com.example.jsync.data.websockets.impls.WebsocketsImpl
import com.example.jsync.domain.auth.repos.AuthRepository
import com.example.jsync.domain.tasks.repos.AskAiRepository
import com.example.jsync.domain.tasks.repos.MainRepository
import com.example.jsync.domain.tasks.repos.TaskCompletionRepo
import com.example.jsync.domain.tasks.repos.TaskRepository
import com.example.jsync.domain.websockets.repo.WebSocketsRepo
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> {
        AuthRepoImplementation()
    }
    single<TaskRepository>{
        TaskRepoImplementation(get())
    }
    single<WebSocketsRepo> {
        WebsocketsImpl(
            get(),
            get() , get()
        )
    }
    single{
        Room.databaseBuilder(
            get() , JSyncDatabase::class.java , "jsync_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<JSyncDatabase>().taskDao()
    }
    single {
        get<JSyncDatabase>().taskCompletionDao()
    }
    single<MainRepository>{
        MainRepoImplementation(
            get() , get() , get() , get(), get() , get()
        )
    }
    single<TaskCompletionRepo>{
        TaskCompletionRepoImpl(
            get() , get() , get() , get() , get()
        )
    }
    single<AskAiRepository>{
        AskAiRepoImplementation()
    }

}

