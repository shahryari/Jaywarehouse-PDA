package com.linari.data.loading

import com.linari.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val loadingModule = module {
    includes(networkModule)

    single<LoadingApi> {
        val retrofit : Retrofit = get()
        retrofit.create(LoadingApi::class.java)
    }

    single {
        LoadingRepository(get())
    }
}