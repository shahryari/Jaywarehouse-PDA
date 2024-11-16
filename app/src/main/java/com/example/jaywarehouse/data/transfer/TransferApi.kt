package com.example.jaywarehouse.data.transfer

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.transfer.models.ProductStatusModel
import com.example.jaywarehouse.data.transfer.models.TransferModel
import com.example.jaywarehouse.data.transfer.models.WarehouseLocationModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TransferApi {

    @POST("TransferList")
    suspend fun getTransferList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<TransferModel>

    @POST("TransferLocation")
    suspend fun transferLocation(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("ProductStatus")
    suspend fun getProductStatus() : Response<ProductStatusModel>

    @POST("WarehouseLocation")
    suspend fun getWarehouseLocations(
        @Body jsonObject: JsonObject
    ) : Response<WarehouseLocationModel>

}
