package com.example.jsync.koin

import com.example.jsync.data.auth.impls.AuthRepoImplementation
import com.example.jsync.domain.auth.repos.AuthRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> {
        AuthRepoImplementation()
    }
}