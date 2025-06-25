package com.example.jaywarehouse.data.shipping

import com.example.jaywarehouse.data.checking.models.PalletStatusModel
import com.example.jaywarehouse.data.common.utils.GenericResultMessageModel
import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.pallet.model.PalletManifestProductModel
import com.example.jaywarehouse.data.picking.models.GetShippingRow
import com.example.jaywarehouse.data.shipping.models.CustomerModel
import com.example.jaywarehouse.data.shipping.models.CustomerRow
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.PalletInShippingModel
import com.example.jaywarehouse.data.shipping.models.PalletMaskModel
import com.example.jaywarehouse.data.shipping.models.PalletTypeModel
import com.example.jaywarehouse.data.shipping.models.ShippingDetailListOfPalletModel
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.example.jaywarehouse.data.shipping.models.ShippingPalletManifestListModel
import com.example.jaywarehouse.data.shipping.models.ShippingPalletManifestRow
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ShippingApi {


    @POST("ShippingTruckList")
    suspend fun getShippings(
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


    @POST("PalletStatus")
    suspend fun getPalletStatuses() : Response<PalletStatusModel>


    @POST("ShippingPalletSubmit")
    suspend fun submitShippingPallet(
        @Body jsonObject: JsonArray
    ): Response<ResultMessageModel>

    @POST("ShippingPalletManifestList")
    suspend fun getShippingPalletManifestList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ShippingPalletManifestListModel>


    @POST("AddPalletToShipping")
    suspend fun addPalletToShipping(
        @Body jsonObject: JsonObject
    ): Response<GenericResultMessageModel<ShippingPalletManifestRow>>

    @POST("CreateShipping")
    suspend fun createShipping(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

    @POST("ShippingDetailListOfPallet")
    suspend fun getShippingDetailListOfPallet(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<ShippingDetailListOfPalletModel>

    @POST("CreateShippingPallet")
    suspend fun createShippingPallet(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

    @POST("UpdateShippingPallet")
    suspend fun updateShippingPallet(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>

    @POST("DeleteShippingPallet")
    suspend fun deleteShippingPallet(
        @Body jsonObject: JsonObject
    ): Response<ResultMessageModel>


    @POST("GetPalletMaskAbbriviation")
    suspend fun getPalletMask(
        @Body jsonObject: JsonObject
    ) : Response<PalletMaskModel>

    @POST("ShippingRollback")
    suspend fun shippingRollback(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("ShippingPalletConfirm")
    suspend fun shippingPalletConfirm(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("GetShipping")
    suspend fun getShipping(
        @Body jsonObject: JsonObject
    ) : Response<GetShippingRow>

    @POST("AddPalletManifestToShipping")
    suspend fun addPalletManifestToShipping(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("RemovePalletManifestFromShipping")
    suspend fun removePalletManifestFromShipping(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>


    @POST("PalletManifestDetail")
    suspend fun getPalletManifestProduct(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PalletManifestProductModel>
}