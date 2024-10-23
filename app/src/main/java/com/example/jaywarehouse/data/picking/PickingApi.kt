package com.example.jaywarehouse.data.picking

import com.example.jaywarehouse.data.picking.models.CustomerToPickModel
import com.example.jaywarehouse.data.picking.models.PickedRemoveModel
import com.example.jaywarehouse.data.picking.models.PickedScanItemsModel
import com.example.jaywarehouse.data.picking.models.ReadyToPickModel
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PickingApi {

    @POST("CustomerToPick")
    suspend fun getCustomerToPick(
        @Body jsonObject: JsonObject,
        @Header("page") page: Int,
        @Header("rows") rows: Int,
        @Header("sort") sort: String,
        @Header("order") order: String
    ): Response<CustomerToPickModel>

    @POST("ReadyToPick")
    suspend fun getReadyToPicked(
        @Body jsonObject: JsonObject,
        @Header("page") page: Int,
        @Header("rows") rows: Int,
        @Header("sort") sort: String,
        @Header("order") order: String
    ): Response<ReadyToPickModel>

    @POST("PickingScan")
    suspend fun scanPicking(
        @Body jsonObject: JsonObject
    ) : Response<ScanModel>

    @POST("PieckedScanItems")
    suspend fun getPickedScanItems(
        @Body jsonObject: JsonObject,
        @Header("page") page: Int,
        @Header("rows") rows: Int,
        @Header("sort") sort: String,
        @Header("order") order: String
    ) : Response<PickedScanItemsModel>

    @POST("PickedRemove")
    suspend fun removePicked(
        @Body jsonObject: JsonObject
    ) : Response<PickedRemoveModel>
}