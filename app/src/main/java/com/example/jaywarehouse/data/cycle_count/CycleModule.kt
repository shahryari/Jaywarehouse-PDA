package com.example.jaywarehouse.data.cycle_count

import com.example.jaywarehouse.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit


val cycleModule = module {
    includes(networkModule)

    single<CycleApi> {
        val retrofit: Retrofit = get()
        retrofit.create(CycleApi::class.java)
    }

    single {
        CycleRepository(get())
    }
}