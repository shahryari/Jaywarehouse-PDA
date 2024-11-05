package com.example.jaywarehouse.data.common.modules

import com.example.jaywarehouse.data.common.utils.API_DOMAIN
import com.example.jaywarehouse.data.common.utils.NoJsonException
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.common.utils.StringConverterFactory
import com.example.jaywarehouse.data.common.utils.StringOrObjectDeserializer
import com.example.jaywarehouse.data.common.utils.restartActivity
import com.google.gson.Gson
import okhttp3.ConnectionPool
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
//                .connectionPool(ConnectionPool(100,60,TimeUnit.SECONDS))
                .readTimeout(30,TimeUnit.SECONDS)
                .connectTimeout(30,TimeUnit.SECONDS)
                .callTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .addInterceptor {
                    var request = it.request()
                    request = request
                        .newBuilder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("Accept","application/json")
//                        .addHeader("App","Mobile")
                        .addHeader("Cookie",prefs.getToken())
                        .build()
                    val response = it.proceed(request).apply {

                        if (body()?.contentType()?.toString()?.contains("text/html") == true) {
                            prefs.setToken("")
                            androidContext().restartActivity()
                        }
                    }
                    response
                }
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