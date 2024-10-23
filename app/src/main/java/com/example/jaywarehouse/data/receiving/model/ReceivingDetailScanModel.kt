package com.example.jaywarehouse.data.receiving.model
import com.google.gson.annotations.SerializedName


data class ReceivingDetailScanModel(
    @SerializedName("EntityID")
    val entityID: Any,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("ReturnValue")
    val returnValue: Int
)