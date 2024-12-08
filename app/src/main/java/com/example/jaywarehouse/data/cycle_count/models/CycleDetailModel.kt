package com.example.jaywarehouse.data.cycle_count.models

import com.google.gson.annotations.SerializedName

data class CycleDetailModel(
    @SerializedName("rows") val rows: List<CycleDetailRow>,
    @SerializedName("total") val total: Int
)

data class CycleDetailRow(
    @SerializedName("ProductID") val productId: Int,
    @SerializedName("ProductName") val productName: String?,
    @SerializedName("ProductCode") val productCode: String?,
    @SerializedName("ProductBarcodeNumber") val productBarcodeNumber: String?,
    @SerializedName("ProductBarcodeID") val productBarcodeId: String?,
    @SerializedName("StockTakingWorkerTaskID") val stockTakingWorkerTaskId: String,
    @SerializedName("WarehouseLocationCode") val warehouseLocationCode: String?,
    @SerializedName("WarehouseLocationID") val warehouseLocationId: Int,
    @SerializedName("Total") val total: Int,
    @SerializedName("Count") val count: Int
)