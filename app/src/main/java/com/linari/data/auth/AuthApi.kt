package com.linari.data.auth

import com.linari.data.auth.models.ChangePasswordModel
import com.linari.data.auth.models.CurrentVersionModel
import com.linari.data.auth.models.DashboardModel
import com.linari.data.auth.models.LoginModel
import com.linari.data.auth.models.WarehouseModel
import com.google.gson.JsonObject
import com.linari.data.common.utils.ResultMessageModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface AuthApi {

    @POST("Login")
    suspend fun login(
        @Body jsonObject: JsonObject
    ): Response<LoginModel>

    @POST("CurrentUser")
    suspend fun getCurrentUser(): Response<LoginModel>

    @POST("ChangePassword")
    suspend fun changePassword(@Body jsonObject: JsonObject): Response<ResultMessageModel>

    @POST("GetCurrentVersionInfo")
    suspend fun getCurrentVersionInfo() : Response<CurrentVersionModel>

    @POST("Dashboard")
    suspend fun getDashboard(
        @Body jsonObject: JsonObject
    ) : Response<DashboardModel>

    @POST("GetWarehouses")
    suspend fun getWarehouses() : Response<List<WarehouseModel>>


    @Multipart
    @POST
    suspend fun uploadFile(
        @Url url: String,
        @Part image: MultipartBody.Part
    ) : Response<ResultMessageModel>

    @POST("CreateVehicleTracking")
    suspend fun createVehicleTracking(
        @Body jsonObject: JsonObject
    ) : Response<ResultMessageModel>
}