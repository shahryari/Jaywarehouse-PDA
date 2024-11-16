package com.example.jaywarehouse.data.transfer.models

import com.example.jaywarehouse.data.common.utils.ROWS
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
)