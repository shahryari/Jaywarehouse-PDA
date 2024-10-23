package com.example.jaywarehouse.data.transfer

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.transfer.models.LocationTransferModel
import com.example.jaywarehouse.data.transfer.models.TransferModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TransferApi {

    @POST("LocationTransfer")
    suspend fun getLocationInventory(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<LocationTransferModel>

    @POST("TransferPick")
    suspend fun pickTransfer(
        @Body jsonObject: JsonObject
    ) : Response<TransferModel>

    @POST("TransferPut")
    suspend fun putTransfer(
        @Body jsonObject: JsonObject
    ) : Response<TransferModel>
}
