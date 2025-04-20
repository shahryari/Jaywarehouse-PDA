package com.example.jaywarehouse.data.manual_putaway.repository

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ManualPutawayDetailModel(
    @SerializedName("rows") val rows: List<ManualPutawayDetailRow>,
    @SerializedName("total") val total: Int,
    @SerializedName("Task") val task: ManualPutawayRow
)
data class ManualPutawayDetailRow(
    @SerializedName("LocationCode") val locationCode: String,
    @SerializedName("Quantity") val quantity: Double,
    @SerializedName("PutawayDetailID") val putawayDetailID: Int
) : Animatable {
    override fun key(): String {
        return putawayDetailID.toString()
    }
}