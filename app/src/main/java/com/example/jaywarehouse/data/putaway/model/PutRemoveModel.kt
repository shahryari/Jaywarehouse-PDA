package com.example.jaywarehouse.data.putaway.model
import com.google.gson.annotations.SerializedName


data class PutRemoveModel(
    @SerializedName("EntityID")
    val entityID: String,
    @SerializedName("IsSucceed")
    val isSucceed: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("ReturnValue")
    val returnValue: Int
)