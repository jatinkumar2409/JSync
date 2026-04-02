package com.example.jsync.koin

import com.example.jsync.presentation.auth.AuthScreenViewModel
import com.example.jsync.presentation.home.MainViewModel
import org.koin.dsl.module

val presentationModule = module {
    single {
        AuthScreenViewModel(get() , get() , get())
    }
    single{
        MainViewModel(get() , get() , get() , get() , get() , get())
    }
    }
