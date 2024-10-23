package com.example.jaywarehouse.data.auth.models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("Username") val username: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("TokenID") val tokenID: String,
    @SerializedName("UserID") val userID: String,
    @SerializedName("Message") val message: String? = null
)