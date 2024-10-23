package com.example.jaywarehouse.data.receiving

import com.example.jaywarehouse.data.common.modules.networkModule
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
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