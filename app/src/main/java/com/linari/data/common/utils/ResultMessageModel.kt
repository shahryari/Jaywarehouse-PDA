package com.linari.data.common.utils

import com.google.gson.annotations.SerializedName


data class ResultMessageModel(
    @SerializedName("EnableClose")
    val enableClose: Boolean,
    @SerializedName("EnableConfirm")
    val enableConfirm: Boolean,
    @SerializedName("EnableInsert")
    val enableInsert: Boolean,
    @SerializedName("EnableUpdate")
    val enableUpdate: Boolean,
    @SerializedName("EntityID")
    val entityID: String?,
    @SerializedName("EntityStringKey")
    val entityStringKey: Any?,
    @SerializedName("ErrorCode")
    val errorCode: Int,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("MessageType")
    val messageType: Int,
    @SerializedName("Messages")
    val messages: List<String>?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("ReturnValue")
    val returnValue: Any?,
    @SerializedName("UpdatedAny")
    val updatedAny: Boolean
)

data class GenericResultMessageModel<T>(
    @SerializedName("EnableClose")
    val enableClose: Boolean,
    @SerializedName("EnableConfirm")
    val enableConfirm: Boolean,
    @SerializedName("EnableInsert")
    val enableInsert: Boolean,
    @SerializedName("EnableUpdate")
    val enableUpdate: Boolean,
    @SerializedName("EntityID")
    val entityID: Any?,
    @SerializedName("EntityStringKey")
    val entityStringKey: Any?,
    @SerializedName("ErrorCode")
    val errorCode: Int,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("MessageType")
    val messageType: Int,
    @SerializedName("Messages")
    val messages: List<String>,
    @SerializedName("ReturnValue")
    val returnValue: T?,
    @SerializedName("UpdatedAny")
    val updatedAny: Boolean
)




