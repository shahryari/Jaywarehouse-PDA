package com.example.jaywarehouse.data.rs

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.rs.models.PODInvoiceModel
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.presentation.common.utils.Order
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RSApi {


    @POST("PODInvoice")
    suspend fun getPodInvoice(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PODInvoiceModel>


    @POST("UpdateDriver")
    suspend fun updateDriver(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("DriverInfo")
    suspend fun driverInfo(
        @Body jsonObject: JsonObject
    ) : Response<DriverModel>

    @POST("RSInterface")
    suspend fun rsInterface(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}