package com.linari.data.receiving.model

import com.google.gson.annotations.SerializedName

data class ReceivingModel(
    @SerializedName("rows") val rows: List<ReceivingRow>,
    @SerializedName("total") val total: Int
)
