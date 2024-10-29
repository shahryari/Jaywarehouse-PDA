package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName

data class ReceivingInceptionDetailModel(
    @SerializedName("rows") val rows: List<ReceivingInceptionDetailRow>,
    @SerializedName("total") val total: Int
)

data class ReceivingInceptionDetailRow(
    @SerializedName("id") val id: Int,
    @SerializedName("qty") val qty: Int,
    @SerializedName("product") val product: String,
    @SerializedName("date") val date: String
)

