package com.linari.data.auth

import com.linari.data.common.modules.networkModule
import com.linari.data.common.modules.prefsModule
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {
    includes(networkModule, prefsModule)

    single {
        val retrofit : Retrofit = get()
        retrofit.create(AuthApi::class.java)
    }

    single {
        AuthRepository(get(),get())
    }
}