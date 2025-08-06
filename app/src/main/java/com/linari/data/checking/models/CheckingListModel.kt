package com.linari.data.checking.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class CheckingListModel(
    @SerializedName("rows")
    val rows: List<CheckingListRow>,
    @SerializedName("total")
    val total: Int
)

data class CheckingListRow(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("BarcodeNumber")
    val barcodeNumber: String?,
    @SerializedName("CheckingWorkerTaskID")
    val checkingWorkerTaskID: Int,
    @SerializedName("CheckingID")
    val checkingID: Int,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("LocationCode")
    val locationCode: String?,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("IsCrossDock")
    val isCrossDock: Boolean,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductLocationActivityID")
    val productLocationActivityID: Int?,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("PurchaseOrderReferenceNumber")
    val purchaseOrderReferenceNumber: String?,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("LocationBase")
    val locationBase: Boolean?,
    @SerializedName("WarehouseID")
    val warehouseID: Int
) : Animatable {
    override fun key(): String {
        return checkingID.toString()
    }
}