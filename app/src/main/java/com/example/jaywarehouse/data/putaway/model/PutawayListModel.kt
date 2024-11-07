package com.example.jaywarehouse.data.putaway.model
import com.google.gson.annotations.SerializedName


data class PutawayListModel(
    @SerializedName("rows")
    val rows: List<PutawayListRow>,
    @SerializedName("total")
    val total: Int,
)

data class PutawayListRow(
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
    @SerializedName("ProductLocationActivityID")
    val productLocationActivityID: Int,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("PutawayWorkerTaskTypeID")
    val putawayWorkerTaskTypeID: Int,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("ReceiptDetailID")
    val receiptDetailID: Int,
    @SerializedName("ReceivingDetailID")
    val receivingDetailID: Int,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String
)