package com.example.jsync.koin

import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.core.helpers.manageToken
import org.koin.dsl.module

val helperModule = module {
    single {
        manageToken(get())
    }
    single {
        NetworkObserver(get())
    }
}