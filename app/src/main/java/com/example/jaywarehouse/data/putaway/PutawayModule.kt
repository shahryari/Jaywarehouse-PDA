package com.example.jaywarehouse.data.putaway

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val putawayModule = module {
    includes(networkModule)

    single<PutawayApi> {
        val retrofit: Retrofit = get()
        retrofit.create(PutawayApi::class.java)
    }

    single {
        PutawayRepository(get())
    }
}