package com.example.jaywarehouse.data.checking

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val checkingModule = module {
    includes(networkModule)


    single<CheckingApi> {
        val retrofit: Retrofit = get()
        retrofit.create(CheckingApi::class.java)
    }

    single {
        CheckingRepository(get())
    }
}