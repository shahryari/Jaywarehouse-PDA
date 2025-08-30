package com.linari.data.putaway.model
import com.google.gson.annotations.SerializedName


data class ScanModel(
    @SerializedName("EntityID")
    val entityID: Long?,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("ReturnValue")
    val returnValue: Int,
    @SerializedName("IsNavigatetoParent")
    val isNavigateToParent: Boolean?
)

data class PutReturnValue(
    @SerializedName("Date")
    val date: String,
    @SerializedName("PutawayID")
    val putawayID: Long,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("Time")
    val time: String
)