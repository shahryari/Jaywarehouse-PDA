package com.example.jaywarehouse.data.cycle_count.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CycleModel (
    @SerializedName("rows") val rows: List<CycleRow>,
    @SerializedName("total") val total: Int
)

data class CycleRow(
    @SerializedName("StockTakingID") val stockTakingID: String,
    @SerializedName("CustomerName") val customerName: String
) : Serializable