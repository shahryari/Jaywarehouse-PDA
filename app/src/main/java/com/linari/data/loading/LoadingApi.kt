package com.linari.data.loading

import com.linari.data.common.utils.ORDER
import com.linari.data.common.utils.PAGE
import com.linari.data.common.utils.ROWS
import com.linari.data.common.utils.ResultMessageModel
import com.linari.data.common.utils.SORT
import com.linari.data.loading.models.LoadingListGroupedModel
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.data.pallet.model.PalletConfirmModel
import com.linari.data.pallet.model.PalletConfirmRow
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoadingApi {


    @POST("LoadingListGrouped")
    suspend fun getLoadingListGrouped(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<LoadingListGroupedModel>

    @POST("LoadingList")
    suspend fun getLoadingList(
        @Body jsonObject: JsonObject,
        @Header(PAGE) page: Int,
        @Header(ROWS) rows: Int,
        @Header(SORT) sort: String,
        @Header(ORDER) order: String
    ) : Response<PalletConfirmModel>

    @POST("LoadingConfirm")
    suspend fun confirmLoading(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}