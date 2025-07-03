package com.hornet

import android.app.Application
import com.hornet.movies.core.di.appModule
import com.hornet.movies.core.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HornetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HornetApp)
            modules(listOf(appModule, networkModule))
        }
    }
}