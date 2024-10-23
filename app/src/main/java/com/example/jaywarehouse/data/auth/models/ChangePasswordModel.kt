package com.example.jaywarehouse.data.auth.models
import com.google.gson.annotations.SerializedName


data class ChangePasswordModel(
    @SerializedName("EntityID")
    val entityID: Int?,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("ReturnValue")
    val returnValue: Int
)