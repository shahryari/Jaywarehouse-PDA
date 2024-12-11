package com.example.jaywarehouse.data.cycle_count

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailModel
import com.example.jaywarehouse.data.cycle_count.models.CycleModel
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
        @Header(ORDER) order: String
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
}