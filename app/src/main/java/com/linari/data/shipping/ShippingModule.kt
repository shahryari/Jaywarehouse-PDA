package com.linari.data.shipping

import com.linari.data.common.modules.networkModule
import org.koin.dsl.module
import retrofit2.Retrofit

val shippingModule = module {
    includes(networkModule)
    single {
        val retrofit : Retrofit = get()
        retrofit.create(ShippingApi::class.java)
    }

    single {
        ShippingRepository(get())
    }
}