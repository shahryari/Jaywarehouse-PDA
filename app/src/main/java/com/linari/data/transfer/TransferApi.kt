package com.linari.data.transfer

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.transfer.models.ProductStatusModel
import com.linari.data.transfer.models.TransferModel
import com.linari.data.transfer.models.WarehouseLocationModel
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
    suspend fun getProductStatus(
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ProductStatusModel>

    @POST("WarehouseLocation")
    suspend fun getWarehouseLocations(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<WarehouseLocationModel>

}
