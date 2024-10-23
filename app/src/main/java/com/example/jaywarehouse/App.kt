package com.example.jaywarehouse

import android.app.Application
import com.example.jaywarehouse.data.common.modules.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(mainModule)
        }
    }
}