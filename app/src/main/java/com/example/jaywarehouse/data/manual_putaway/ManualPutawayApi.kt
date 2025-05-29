package com.example.jaywarehouse.data.manual_putaway

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.manual_putaway.models.ManualPutawayDetailModel
import com.example.jaywarehouse.data.manual_putaway.models.ManualPutawayModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ManualPutawayApi {



    @POST("PutawayListManual")
    suspend fun getManualPutawayList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ): Response<ManualPutawayModel>


    @POST("PutawayManualScan")
    suspend fun putawayManualScan(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

    @POST("PutawayManualRemove")
    suspend fun putawayManualRemove(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

    @POST("PutawayFinishManual")
    suspend fun putawayManualFinish(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>


    @POST("PutawayManualScanedItem")
    suspend fun getManualPutawayDetail(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ManualPutawayDetailModel>

    @POST("PutawayManualDone")
    suspend fun putawayManualDone(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

}