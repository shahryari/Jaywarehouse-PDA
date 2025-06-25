package com.linari.data.transfer.models

import com.google.gson.annotations.SerializedName


data class WarehouseLocationModel(
    @SerializedName("rows") val rows: List<WarehouseLocationRow>,
    @SerializedName("total") val total: Int
)

data class WarehouseLocationRow(
    @SerializedName("WarehouseLocationID")
    val locationId: Int,
    @SerializedName("WarehouseLocationCode")
    val locationCode: String
) {
    override fun toString(): String {
        return locationCode
    }
}