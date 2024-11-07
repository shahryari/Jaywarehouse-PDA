package com.example.jaywarehouse.data.putaway

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.putaway.model.PutawayListModel
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PutawayApi {

    @POST("PutawayListGrouped")
    suspend fun getPutawayListGrouped(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PutawayListGroupedModel>


    @POST("PutawayList")
    suspend fun getPutawayList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PutawayListModel>

    @POST("PutawayFinish")
    suspend fun finishPutaway(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}