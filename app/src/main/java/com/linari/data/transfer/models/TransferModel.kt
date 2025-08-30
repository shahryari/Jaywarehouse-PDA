package com.linari.data.transfer.models
import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class TransferModel(
    @SerializedName("rows")
    val rows: List<TransferRow>,
    @SerializedName("total")
    val total: Int
)

data class TransferRow(
    @SerializedName("ProductID")
    val productID: Long,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductBarcodeID")
    val productBarcodeID: String,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String?,
    @SerializedName("LocationInventoryID")
    val locationInventoryID: Long,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?,
    @SerializedName("WarehouseLocationID")
    val warehouseLocationID: Long,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("ExpireDate")
    val expireDate: String?,
    @SerializedName("AvailableInventory")
    val availableInventory: Double,
    @SerializedName("RealInventory")
    val realInventory: Double,
    @SerializedName("WarehouseID")
    val warehouseID: Long,
    @SerializedName("OwnerInfoID")
    val ownerInfoID: String?,
    @SerializedName("OwnerInfoFullName")
    val ownerInfoFullName: String?
) : Animatable {
    override fun key(): String {
        return "${ownerInfoID}_${locationInventoryID}_${warehouseLocationID}_$productBarcodeID"
    }
}


