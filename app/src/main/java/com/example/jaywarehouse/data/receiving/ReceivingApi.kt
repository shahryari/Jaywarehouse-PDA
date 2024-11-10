package com.example.jaywarehouse.data.receiving

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailGetItemsModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.data.receiving.model.ReceivingModel
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
    ) : Response<ReceivingDetailScanModel>

    @POST("ReceivingDetailCountGetItems")
    suspend fun getReceivingDetailCountItems(
        @Body jsonObject: JsonObject,

        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReceivingDetailGetItemsModel>


    @POST("ReceivingDetailCountConfirm")
    suspend fun confirmReceivingDetailCount(
        @Body jsonObject: JsonObject
    ) : Response<ReceivingDetailScanModel>
}