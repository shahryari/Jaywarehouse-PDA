package com.linari.data.common.modules

import com.google.gson.Gson
import com.linari.data.common.utils.API_DOMAIN
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.restartActivity
import okhttp3.Dns
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val networkModule = module {
    includes(prefsModule)
    single<OkHttpClient> {
        try {
            val prefs: Prefs = get()
            OkHttpClient.Builder()
                .readTimeout(45,TimeUnit.SECONDS)
                .connectTimeout(45,TimeUnit.SECONDS)
                .callTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(45,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor {

                    var originalReuest = it.request()
                    val request = originalReuest
                        .newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("App", "Mobile")
                        .addHeader("User-Agent", "Mobile")
                        .addHeader("Cookie", prefs.getToken().orEmpty())
                        .build()

                    val response = it.proceed(request).apply {

                        if (body?.contentType()?.toString()?.contains("text/html") == true) {
                            prefs.setToken("")
                            androidContext().restartActivity()
                        }
                    }
                    response

                }
                .dns(Dns.SYSTEM)
                .build()
        } catch (e: Exception) {
            OkHttpClient()
        }
    }

    single<Retrofit> {
        val gson  = Gson().newBuilder()
            .setLenient()
            .create()
        val prefs : Prefs = get()
        Retrofit.Builder().apply {
            baseUrl(prefs.getAddress()+API_DOMAIN)
            addConverterFactory(GsonConverterFactory.create(gson))
            client(get())
        }.build()

    }
}