package com.example.jsync.koin

import com.example.jsync.domain.auth.usecases.SignInUseCase
import com.example.jsync.domain.auth.usecases.SignUpUseCase
import org.koin.dsl.module

val domainModule = module {
    single {
        SignInUseCase(get() , get())
    }
    single {
        SignUpUseCase(get() , get())
    }
}