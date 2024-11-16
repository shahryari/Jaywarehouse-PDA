package com.example.jaywarehouse.data.transfer.models
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
    val productBarcodeNumber: String,
    @SerializedName("LocationInventoryID")
    val locationInventoryID: Int,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String,
    @SerializedName("WarehouseLocationID")
    val warehouseLocationID: Int,
    @SerializedName("BatchNumber")
    val batchNumber: String,
    @SerializedName("ExpireDate")
    val expireDate: String,
    @SerializedName("AvailableInventory")
    val availableInventory: Int,
    @SerializedName("RealInventory")
    val realInventory: Int,
    @SerializedName("WarehouseID")
    val warehouseID: Int,
    @SerializedName("OwnerInfoID")
    val ownerInfoID: Int,
    @SerializedName("OwnerInfoFullName")
    val ownerInfoFullName: String
)


