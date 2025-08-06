package com.linari

import android.app.Application
import coil.ImageLoader
import coil.util.DebugLogger
import com.linari.data.common.modules.mainModule
import com.jakewharton.threetenabp.AndroidThreeTen
import com.linari.data.common.utils.Prefs
import okhttp3.Headers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(mainModule)
        }
    }

}