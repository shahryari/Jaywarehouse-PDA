package com.example.jaywarehouse.data.rs

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val rSModule = module {
    includes(networkModule)

    single<RSApi> {
        val retrofit: Retrofit = get()
        retrofit.create(RSApi::class.java)
    }

    single {
        RSRepository(get())
    }
}