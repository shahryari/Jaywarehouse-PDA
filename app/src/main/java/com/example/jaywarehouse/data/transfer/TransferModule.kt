package com.example.jaywarehouse.data.transfer

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val transferModule = module {
    includes(networkModule)
    single {
        val retrofit: Retrofit = get()
        retrofit.create(TransferApi::class.java)
    }

    single {
        TransferRepository(get())
    }
}