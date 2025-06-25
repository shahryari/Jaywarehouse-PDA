package com.linari.data.receiving

import com.linari.data.common.modules.networkModule
import com.linari.data.receiving.repository.ReceivingRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val receivingModule = module {
    includes(networkModule)

    single {
        val retrofit : Retrofit = get()
        retrofit.create(ReceivingApi::class.java)
    }

    single {
        ReceivingRepository(get())
    }
}