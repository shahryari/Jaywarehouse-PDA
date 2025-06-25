package com.linari.data.manual_putaway.models

import com.linari.presentation.common.composables.Animatable
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