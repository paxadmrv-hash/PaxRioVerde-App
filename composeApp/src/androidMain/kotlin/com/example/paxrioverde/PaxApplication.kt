package com.example.paxrioverde

import android.app.Application
import com.example.paxrioverde.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PaxApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PaxApplication)
            modules(appModule)
        }
    }
}
