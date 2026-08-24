package com.example.paxrioverde

import android.app.Application
import com.example.paxrioverde.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PaxApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@PaxApplication)
        }
    }
}
