package com.linari.data.picking

import org.koin.dsl.module
import retrofit2.Retrofit

val pickingModule = module {

    single<PickingApi> {
        val retrofit: Retrofit = get()
        retrofit.create(PickingApi::class.java)
    }
    single {
        PickingRepository(get())
    }
}