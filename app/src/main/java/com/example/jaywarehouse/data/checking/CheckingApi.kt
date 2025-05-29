package com.example.jaywarehouse.data.checking

import com.example.jaywarehouse.data.checking.models.CheckingListGroupedModel
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.checking.models.PalletStatusModel
import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.PAGE
import com.example.jaywarehouse.data.common.utils.ROWS
import com.example.jaywarehouse.data.common.utils.ResultMessageModel
import com.example.jaywarehouse.data.common.utils.SORT
import com.example.jaywarehouse.data.shipping.models.PalletTypeModel
import com.google.gson.JsonObject
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
    suspend fun getPalletTypes() : Response<PalletTypeModel>

    @POST("PalletStatus")
    suspend fun getPalletStatuses() : Response<PalletStatusModel>
}