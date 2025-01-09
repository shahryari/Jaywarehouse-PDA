package com.example.jaywarehouse.data.cycle_count.models

import com.google.gson.annotations.SerializedName

data class CycleDetailModel(
    @SerializedName("rows") val rows: List<CycleDetailRow>,
    @SerializedName("total") val total: Int
)
data class CycleDetailRow(
    @SerializedName("Aisle")
    val aisle: String,
    @SerializedName("Bank")
    val bank: String,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("Bay")
    val bay: String,
    @SerializedName("BookedQuantity")
    val bookedQuantity: Int,
    @SerializedName("CountQuantity")
    val countQuantity: Int?,
    @SerializedName("CycleCountID")
    val cycleCountID: String,
    @SerializedName("CycleCountLocationID")
    val cycleCountLocationID: String,
    @SerializedName("CycleCountWorkerTaskID")
    val cycleCountWorkerTaskID: String,
    @SerializedName("ExpireDate")
    val expireDate: String?,
    @SerializedName("LevelInfo")
    val levelInfo: String,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("ProductBarcodeID")
    val productBarcodeID: Int,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductID")
    val productID: Int,
    @SerializedName("ProductTitle")
    val productTitle: String,
    @SerializedName("QuiddityTypeID")
    val quiddityTypeID: Int,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String,
    @SerializedName("CycleCountWorkerTaskDetailID")
    val cycleCountWorkerTaskDetailID: String,
    @SerializedName("Counting")
    val counting: Int,
) {
    override fun equals(other: Any?): Boolean {
        return other is CycleDetailRow
                && cycleCountWorkerTaskDetailID == other.cycleCountWorkerTaskDetailID
                && cycleCountWorkerTaskID == other.cycleCountWorkerTaskID
                && productBarcodeNumber == other.productBarcodeNumber
                && productID == other.productID
                && cycleCountLocationID == other.cycleCountLocationID
                && cycleCountID == other.cycleCountID
                && productCode == other.productCode
                && productBarcodeID == other.productBarcodeID
    }
}