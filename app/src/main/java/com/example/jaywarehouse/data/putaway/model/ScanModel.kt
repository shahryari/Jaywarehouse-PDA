package com.example.jaywarehouse.data.putaway.model
import com.google.gson.annotations.SerializedName


data class ScanModel(
    @SerializedName("EntityID")
    val entityID: Int?,
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
    val putawayID: Int,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("Time")
    val time: String
)