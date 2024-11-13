package com.example.jaywarehouse.data.loading

import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedModel
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmModel
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
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