package com.example.jsync.koin

import com.example.jsync.presentation.auth.AuthScreenViewModel
import com.example.jsync.presentation.home.AskAiViewModel
import com.example.jsync.presentation.home.MainViewModel
import com.example.jsync.presentation.home.TaskCompletionsViewModel
import com.example.jsync.presentation.home.TasksViewModel
import org.koin.dsl.module

val presentationModule = module {
    single {
        AuthScreenViewModel(get() , get() , get())
    }
    single{
        MainViewModel(get() , get() , get() , get() , get() , get())
    }
    single{
        AskAiViewModel(get())
    }
    single{
        TasksViewModel(
            get() , get() , get() , get()
        )
    }
    single{
        TaskCompletionsViewModel(
            get() , get()
        )
    }
    }
