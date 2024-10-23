package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName

data class ReceivingDetailModel(
    @SerializedName("rows")
    val rows: List<ReceivingDetailRow>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("Receiving")
    val receiving: ReceivingRow?
)