package com.example.jaywarehouse.data.manual_putaway.repository

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
    @SerializedName("ExpireDateString")
    val expireDateString: String?,
    @SerializedName("OwnerFullName")
    val ownerFullName: String,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductID")
    val productID: Int,
    @SerializedName("ProductInventoryID")
    val productInventoryID: Int,
    @SerializedName("ProductLocationActivityID")
    val productLocationActivityID: String?,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("PutawayWorkerTaskTypeID")
    val putawayWorkerTaskTypeID: Int,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("ReceiptDetailID")
    val receiptDetailID: Int,
    @SerializedName("ReceiptID")
    val receiptID: Int,
    @SerializedName("ReceivingDetailID")
    val receivingDetailID: Int,
    @SerializedName("WarehouseID")
    val warehouseID: Int,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?
) : Serializable