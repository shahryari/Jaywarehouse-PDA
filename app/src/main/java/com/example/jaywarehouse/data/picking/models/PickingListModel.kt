package com.example.jaywarehouse.data.picking.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class PickingListModel(
    @SerializedName("rows")
    val rows: List<PickingListRow>,
    @SerializedName("total")
    val total: Int
)

data class PickingListRow(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("BarcodeNumber")
    val barcodeNumber: String,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("PickingID")
    val pickingID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ExpireDate")
    val productInventoryExpireDate: String?,
    @SerializedName("ProductInventoryHistoryID")
    val productInventoryHistoryID: Int?,
    @SerializedName("ProductLocationActivityID")
    val productLocationActivityID: Int,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Int,
    @SerializedName("TypeofOrderAcquisition")
    val typeofOrderAcquisition: String?,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String
) : Animatable {
    override fun key(): String {
        return pickingID.toString()
    }

}