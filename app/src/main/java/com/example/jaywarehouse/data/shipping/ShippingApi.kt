package com.example.jaywarehouse.data.shipping

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.shipping.models.CustomerModel
import com.example.jaywarehouse.data.shipping.models.CustomerRow
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.PalletInShippingModel
import com.example.jaywarehouse.data.shipping.models.PalletTypeModel
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ShippingApi {


    @POST("ShippingTruckList")
    suspend fun getShipping(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ShippingModel>

    @POST("PalletListInShipping")
    suspend fun getPalletListInShipping(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PalletInShippingModel>

    @POST("ShippingConfirm")
    suspend fun confirmShipping(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("CreteInvoice")
    suspend fun createInvoice(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("CreateRSInterface")
    suspend fun createRSInterface(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("PalletBarcodeCheck")
    suspend fun palletBarcodeCheck(
        @Body jsonObject: JsonObject
    ) : Response<PalletConfirmRow>

    @POST("DriverInfo")
    suspend fun getDriverInfo(
        @Body jsonObject: JsonObject
    ) : Response<DriverModel>


    @POST("ShippingSubmit")
    suspend fun submitShipping(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>


    @POST("ShippingCustomer")
    suspend fun getShippingCustomers(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<CustomerModel>

    @POST("PalletType")
    suspend fun getShippingPalletType(
//        @Body jsonObject: JsonObject,
//        @Header(PAGE) page: Int,
//        @Header(ROWS) rows: Int,
//        @Header(SORT) sort: String,
//        @Header(ORDER) order: String
    ) : Response<PalletTypeModel>


    @POST("ShippingPalletSubmit")
    suspend fun submitShippingPallet(
        @Body jsonObject: JsonArray
    ): Response<ResultMessageModel>

}