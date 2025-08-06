package com.linari.data.rs

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.rs.models.PODInvoiceModel
import com.linari.data.shipping.models.DriverModel
import com.linari.presentation.common.utils.Order
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.linari.data.rs.models.WaybillInfoModel
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

    @POST("WaybillInfoes")
    suspend fun waybillInfoes(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<WaybillInfoModel>

    @POST("IntegerateWithRS")
    suspend fun integerateWithRS(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}