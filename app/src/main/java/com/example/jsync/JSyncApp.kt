package com.example.jsync

import android.app.Application
import com.example.jsync.koin.dataModule
import com.example.jsync.koin.domainModule
import com.example.jsync.koin.helperModule
import com.example.jsync.koin.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class JSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(){
            androidContext(this@JSyncApp)
            modules(dataModule , helperModule , domainModule , presentationModule)
        }
    }
}