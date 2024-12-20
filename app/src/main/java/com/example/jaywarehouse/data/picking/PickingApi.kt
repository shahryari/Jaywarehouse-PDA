package com.example.jaywarehouse.data.picking

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.picking.models.PickingListGroupedModel
import com.example.jaywarehouse.data.picking.models.PickingListModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PickingApi {

    @POST("PickingListGrouped")
    suspend fun getPickingListGrouped(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PickingListGroupedModel>

    @POST("PickingList")
    suspend fun getPickingList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PickingListModel>

    @POST("PickingComplete")
    suspend fun completePicking(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}