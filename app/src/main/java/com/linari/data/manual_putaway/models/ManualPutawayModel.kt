package com.linari.data.manual_putaway.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ManualPutawayModel(
    @SerializedName("rows")
    val rows: List<ManualPutawayRow>,
    @SerializedName("total")
    val total: Int
)


data class ManualPutawayRow(
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("ExpDate")
    val expireDate: String?,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductInventoryID")
    val productInventoryID: Int,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("PutawayID")
    val putawayID: Int,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("ReceiptDetailID")
    val receiptDetailID: Int,
    @SerializedName("Total")
    val total: Double,
    @SerializedName("WarehouseID")
    val warehouseID: Int,
    @SerializedName("CreatedBy")
    val createdBy: String?,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?,
    @SerializedName("WarehouseName")
    val warehouseName: String?,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String?,
    @SerializedName("IsWeight")
    val isWeight: Boolean
) : Serializable, Animatable {
    override fun key(): String {
        return putawayID.toString()
    }
}