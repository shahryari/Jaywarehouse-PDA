package com.example.jaywarehouse.data.manual_putaway.repository

import com.example.jaywarehouse.presentation.common.composables.Animatable
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
    @SerializedName("ExpireDate")
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
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?
) : Serializable, Animatable {
    override fun key(): String {
        return putawayID.toString()
    }
}