package com.example.jaywarehouse.data.auth.models
import com.google.gson.annotations.SerializedName


data class LoginErrorModel(
    @SerializedName("Message")
    val message: String
)