package com.example.jaywarehouse.data.auth

import com.example.jaywarehouse.data.auth.models.ChangePasswordModel
import com.example.jaywarehouse.data.auth.models.CurrentVersionModel
import com.example.jaywarehouse.data.auth.models.LoginModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body jsonObject: JsonObject
    ): Response<LoginModel>

    @POST("CurrentUser")
    suspend fun getCurrentUser(): Response<LoginModel>

    @POST("ChangePassword")
    suspend fun changePassword(@Body jsonObject: JsonObject): Response<ChangePasswordModel>

    @POST("GetCurrentVersionInfo")
    suspend fun getCurrentVersionInfo() : Response<CurrentVersionModel>
}