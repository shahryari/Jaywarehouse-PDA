package com.linari.data.receiving

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.receiving.model.ReceivingDetailCountModel
import com.linari.data.receiving.model.ReceivingDetailGetItemsModel
import com.linari.data.receiving.model.ReceivingDetailModel
import com.linari.data.receiving.model.ReceivingModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ReceivingApi {

    @POST("Receiving")
    suspend fun getReceivingList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReceivingModel>

    @POST("ReceivingDetail")
    suspend fun getReceivingDetails(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReceivingDetailModel>

    @POST("ReceivingDetailCount")
    suspend fun countReceivingDetail(
        @Body jsonObject: JsonObject,
    ) : Response<ResultMessageModel>

    @POST("ReceivingDetailCountGetItems")
    suspend fun getReceivingDetailCountItems(
        @Body jsonObject: JsonObject,

        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReceivingDetailGetItemsModel>


    @POST("ReceivingWorkerTaskCountInsert")
    suspend fun receivingWorkerTaskCountInsert(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("ReceivingWorkerTaskCountDelete")
    suspend fun receivingWorkerTaskCountDelete(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("ReceivingWorkerTaskDone")
    suspend fun receivingWorkerTaskDone(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}