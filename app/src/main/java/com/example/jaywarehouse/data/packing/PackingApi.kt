package com.example.jaywarehouse.data.packing

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.packing.model.PackingCustomerModel
import com.example.jaywarehouse.data.packing.model.PackingDetailModel
import com.example.jaywarehouse.data.packing.model.PackingModel
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PackingApi {

    @POST("PackingCustomers")
    suspend fun getPackingCustomer(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PackingCustomerModel>

    @POST("Packing")
    suspend fun getPacking(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PackingModel>

    @POST("PackingDetails")
    suspend fun getPackingDetails(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PackingDetailModel>

    @POST("Pack")
    suspend fun pack(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>

    @POST("PackRemove")
    suspend fun packRemove(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>

    @POST("PackingDetailAdd")
    suspend fun addPackingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>

    @POST("PackingDetailRemove")
    suspend fun removePackingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>

    @POST("PackingFinish")
    suspend fun finishPacking(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>
}