package com.example.jaywarehouse.data.pallet

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.pallet.model.PalletConfirmModel
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
}