package com.linari.data.pallet

import com.linari.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val palletModule = module {
    includes(networkModule)


    single<PalletApi> {
        val retrofit: Retrofit = get()
        retrofit.create(PalletApi::class.java)
    }

    single {
        PalletRepository(get())
    }
}