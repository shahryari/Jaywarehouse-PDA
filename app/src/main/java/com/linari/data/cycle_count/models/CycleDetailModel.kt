package com.linari.data.cycle_count.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class CycleDetailModel(
    @SerializedName("rows") val rows: List<CycleDetailRow>,
    @SerializedName("total") val total: Int
)
data class CycleDetailRow(
    @SerializedName("Aisle")
    val aisle: String?,
    @SerializedName("Bank")
    val bank: String?,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("Bay")
    val bay: String?,
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
    val levelInfo: String?,
    @SerializedName("LocationCode")
    val locationCode: String?,
    @SerializedName("ProductBarcodeID")
    val productBarcodeID: Long,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductID")
    val productID: Long,
    @SerializedName("ProductTitle")
    val productTitle: String,
    @SerializedName("QuiddityTypeID")
    val quiddityTypeID: Long?,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String?,
    @SerializedName("CycleCountWorkerTaskDetailID")
    val cycleCountWorkerTaskDetailID: String,
    @SerializedName("Counting")
    val counting: Int,
) : Animatable{
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

    override fun key(): String {
        return cycleCountWorkerTaskDetailID
    }

    override fun hashCode(): Int {
        var result : Long= bookedQuantity.toLong()
        result = 31 * result + (countQuantity ?: 0)
        result = 31 * result + productBarcodeID
        result = 31 * result + productID
        result = 31 * result + (quiddityTypeID?:0)
        result = 31 * result + counting
        result = 31 * result + aisle.hashCode()
        result = 31 * result + bank.hashCode()
        result = 31 * result + (batchNumber?.hashCode() ?: 0)
        result = 31 * result + bay.hashCode()
        result = 31 * result + cycleCountID.hashCode()
        result = 31 * result + cycleCountLocationID.hashCode()
        result = 31 * result + cycleCountWorkerTaskID.hashCode()
        result = 31 * result + (expireDate?.hashCode() ?: 0)
        result = 31 * result + levelInfo.hashCode()
        result = 31 * result + locationCode.hashCode()
        result = 31 * result + productBarcodeNumber.hashCode()
        result = 31 * result + productCode.hashCode()
        result = 31 * result + productTitle.hashCode()
        result = 31 * result + quiddityTypeTitle.hashCode()
        result = 31 * result + cycleCountWorkerTaskDetailID.hashCode()
        return result.toInt()
    }
}