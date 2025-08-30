package com.linari.data.return_receiving

import com.google.gson.JsonObject
import com.linari.data.checking.models.PalletStatusModel
import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.receiving.model.ReceivingDetailModel
import com.linari.data.return_receiving.models.CustomerModel
import com.linari.data.return_receiving.models.OwnerInfoModel
import com.linari.data.return_receiving.models.ReturnDetailModel
import com.linari.data.return_receiving.models.ReturnModel
import com.linari.data.shipping.models.ShippingCustomerModel
import com.linari.data.transfer.models.ProductStatusModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ReturnApi {

    @POST("ReturnReceivingList")
    suspend fun getReturnReceivingList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReturnModel>

    @POST("ReturnReceivingDetailList")
    suspend fun getReceivingDetails(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ReturnDetailModel>

    @POST("RemoveReturnReceiving")
    suspend fun removeReturnReceiving(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("RemoveReturnReceivingDetail")
    suspend fun removeReturnReceivingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("SaveReturnReceiving")
    suspend fun saveReturnReceiving(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("SaveReturnReceivingDetail")
    suspend fun saveReturnReceivingDetail(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("CustomerList")
    suspend fun getCustomerList() : Response<CustomerModel>

    @POST("OwnerInfoList")
    suspend fun getOwnerInfoList() : Response<OwnerInfoModel>


    @POST("ProductStatus")
    suspend fun getProductStatus(
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ProductStatusModel>

}