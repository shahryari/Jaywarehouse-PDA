package com.example.jaywarehouse.data.putaway.model
import com.example.jaywarehouse.presentation.common.composables.Animatable
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
    @SerializedName("ExpireDate")
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
    val quantity: Double,
    @SerializedName("ReceiptDetailID")
    val receiptDetailID: Int,
    @SerializedName("ReceivingDetailID")
    val receivingDetailID: Int,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String
) : Animatable {
    override fun key(): String {
        return receiptDetailID.toString()
    }

}