package com.example.jsync.koin

import com.example.jsync.domain.auth.usecases.SignInUseCase
import com.example.jsync.domain.auth.usecases.SignUpUseCase
import com.example.jsync.domain.tasks.usecases.AddTaskUseCase
import com.example.jsync.domain.tasks.usecases.LoadTasksUseCase
import org.koin.dsl.module

val domainModule = module {
    single {
        SignInUseCase(get() , get() , get())
    }
    single {
        SignUpUseCase(get() , get() , get())
    }
    single {
        LoadTasksUseCase(get())
    }
    single {
        AddTaskUseCase(get())
    }
}