package com.example.jaywarehouse.data.putaway

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.putaway.model.PutRemoveModel
import com.example.jaywarehouse.data.putaway.model.PutawaysModel
import com.example.jaywarehouse.data.putaway.model.ReadyToPutModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PutawayApi {

    @POST("ReadyToPut")
    suspend fun getReadyToPut(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReadyToPutModel>

    @POST("Put")
    suspend fun put(
        @Body jsonObject: JsonObject,
//        @Header(PAGE) page: Int,
//        @Header(ROWS) rows: Int,
//        @Header(SORT) sort: String,
//        @Header(ORDER) order: String
    ) : Response<ScanModel>

    @POST("Putaways")
    suspend fun getPutaways(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PutawaysModel>

    @POST("PutRemove")
    suspend fun putRemove(
        @Body jsonObject: JsonObject,
//        @Header(PAGE) page: Int,
//        @Header(ROWS) rows: Int,
//        @Header(SORT) sort: String,
//        @Header(ORDER) order: String
    ) : Response<PutRemoveModel>
}