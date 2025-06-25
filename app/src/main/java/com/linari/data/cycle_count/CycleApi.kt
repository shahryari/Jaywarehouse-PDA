package com.linari.data.cycle_count

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.cycle_count.models.CycleDetailModel
import com.linari.data.cycle_count.models.CycleModel
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CycleApi {


    @POST("CycleCountLocation")
    suspend fun getCycleCountLocations(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
    ) : Response<CycleModel>

    @POST("CycleCountLocationDetail")
    suspend fun getCycleCountLocationDetail(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<CycleDetailModel>


    @POST("InsertTaskDetail")
    suspend fun insertTaskDetail(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>


    @POST("DetailUpdateQuantity")
    suspend fun updateQuantity(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("LocationTaskEnd")
    suspend fun locationTaskEnd(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}