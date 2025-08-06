package com.linari.data.checking

import com.linari.data.checking.models.CheckingListGroupedModel
import com.linari.data.checking.models.CheckingListModel
import com.linari.data.checking.models.PalletStatusModel
import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.shipping.models.PalletMaskModel
import com.linari.data.shipping.models.PalletTypeModel
import com.google.gson.JsonObject
import com.linari.data.checking.models.PalletManifestInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CheckingApi {


    @POST("CheckingListGrouped")
    suspend fun getCheckingListGrouped(
        @Body jsonObject: JsonObject,

        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<CheckingListGroupedModel>

    @POST("CheckingList")
    suspend fun getCheckingList(
        @Body jsonObject: JsonObject,

        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<CheckingListModel>


    @POST("CheckingDo")
    suspend fun checking(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>

    @POST("PalletType")
    suspend fun getPalletTypes(@Body jsonObject: JsonObject) : Response<PalletTypeModel>

    @POST("PalletStatus")
    suspend fun getPalletStatuses() : Response<PalletStatusModel>


    @POST("GetPalletMaskAbbriviation")
    suspend fun getPalletMask(
        @Body jsonObject: JsonObject
    ) : Response<PalletMaskModel>

    @POST("PalletManifestInfo")
    suspend fun getPalletManifestInfo(
        @Body jsonObject: JsonObject
    ) : Response<PalletManifestInfo>


    @POST("PickCancel")
    suspend fun cancelPick(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}