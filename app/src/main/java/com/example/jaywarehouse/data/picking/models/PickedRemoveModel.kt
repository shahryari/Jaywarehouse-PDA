package com.example.jaywarehouse.data.picking.models
import com.google.gson.annotations.SerializedName


data class PickedRemoveModel(
    @SerializedName("Message")
    val message: String
)