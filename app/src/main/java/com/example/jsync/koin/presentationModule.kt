package com.example.jsync.koin

import com.example.jsync.presentation.auth.AuthScreenViewModel
import org.koin.dsl.module

val presentationModule = module {
    single {
        AuthScreenViewModel(get() , get() , get())
    }
}