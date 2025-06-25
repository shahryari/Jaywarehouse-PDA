package com.linari.data.pallet

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.pallet.model.PalletConfirmModel
import com.linari.data.pallet.model.PalletManifestProductModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PalletApi {

    @POST("PalletManifestList")
    suspend fun getPalletManifestList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PalletConfirmModel>


    @POST("PalletManifestComplete")
    suspend fun completePalletManifest(
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