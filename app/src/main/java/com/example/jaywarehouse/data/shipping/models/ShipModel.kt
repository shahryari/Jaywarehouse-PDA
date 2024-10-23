package com.example.jaywarehouse.data.shipping.models
import com.google.gson.annotations.SerializedName


data class ShipModel(
    @SerializedName("EnableClose")
    val enableClose: Boolean,
    @SerializedName("EnableConfirm")
    val enableConfirm: Boolean,
    @SerializedName("EnableInsert")
    val enableInsert: Boolean,
    @SerializedName("EnableUpdate")
    val enableUpdate: Boolean,
    @SerializedName("EntityID")
    val entityID: Int?,
    @SerializedName("EntityStringKey")
    val entityStringKey: String?,
    @SerializedName("ErrorCode")
    val errorCode: Int,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("MessageType")
    val messageType: Int,
    @SerializedName("Messages")
    val messages: List<String>,
    @SerializedName("ReturnValue")
    val returnValue: Int,
    @SerializedName("UpdatedAny")
    val updatedAny: Boolean
)