package com.example.jaywarehouse.data.transfer.models
import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class TransferModel(
    @SerializedName("rows")
    val rows: List<TransferRow>,
    @SerializedName("total")
    val total: Int
)

data class TransferRow(
    @SerializedName("ProductID")
    val productID: Int,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductBarcodeID")
    val productBarcodeID: String,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String?,
    @SerializedName("LocationInventoryID")
    val locationInventoryID: Int,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?,
    @SerializedName("WarehouseLocationID")
    val warehouseLocationID: Int,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("ExpireDate")
    val expireDate: String?,
    @SerializedName("AvailableInventory")
    val availableInventory: Double,
    @SerializedName("RealInventory")
    val realInventory: Double,
    @SerializedName("WarehouseID")
    val warehouseID: Int,
    @SerializedName("OwnerInfoID")
    val ownerInfoID: String?,
    @SerializedName("OwnerInfoFullName")
    val ownerInfoFullName: String?
) : Animatable {
    override fun key(): String {
        return "${ownerInfoID}_${locationInventoryID}_${warehouseLocationID}_$productBarcodeID"
    }
}


