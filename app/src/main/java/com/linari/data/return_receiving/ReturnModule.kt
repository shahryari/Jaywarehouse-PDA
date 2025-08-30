package com.linari.data.return_receiving

import org.koin.dsl.module
import retrofit2.Retrofit

val returnModule = module {
    single<ReturnApi>{
        val retrofit: Retrofit = get()
        retrofit.create(ReturnApi::class.java)
    }

    single {
        ReturnRepository(get())
    }
}