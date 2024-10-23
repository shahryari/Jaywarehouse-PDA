package com.example.jaywarehouse.data.packing

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val packingModule = module {
    includes(networkModule)
    single {
        val retrofit : Retrofit = get()
        retrofit.create(PackingApi::class.java)
    }

    single {
        PackingRepository(get())
    }
}