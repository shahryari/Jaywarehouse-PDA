package com.example.jaywarehouse.data.manual_putaway.repository

import com.google.gson.annotations.SerializedName

data class ManualPutawayDetailModel(
    @SerializedName("rows") val rows: List<ManualPutawayDetailRow>,
    @SerializedName("total") val total: Int
)
data class ManualPutawayDetailRow(
    @SerializedName("ProductLocationActivityID")val productLocationActivityId: String,
    @SerializedName("WarehouseLocationCode") val warehouseLocationCode: String,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("CreatedOn") val createdOn: String
)