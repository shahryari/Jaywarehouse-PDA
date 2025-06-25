package com.linari.data.transfer

import com.linari.data.common.modules.networkModule
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