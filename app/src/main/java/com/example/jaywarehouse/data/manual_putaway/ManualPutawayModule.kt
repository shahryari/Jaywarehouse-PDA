package com.example.jaywarehouse.data.manual_putaway

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit


val manualPutawayModule = module {
    includes(networkModule)

    single<ManualPutawayApi> {
        val retrofit: Retrofit = get()
        retrofit.create(ManualPutawayApi::class.java)
    }

    single {
        ManualPutawayRepository(get())
    }
}