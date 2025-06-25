package com.linari.data.picking.models

import com.linari.presentation.common.composables.Animatable
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
    val barcodeNumber: String?,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("ExpDate")
    val expDate: String?,
    @SerializedName("PickingID")
    val pickingID: Int,
    @SerializedName("ProductCode")
    val productCode: String?,
    @SerializedName("ProductName")
    val productName: String?,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("PurchaseOrderDetailID")
    val purchaseOrderDetailID: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Int,
    @SerializedName("TypeofOrderAcquisition")
    val typeofOrderAcquisition: String?,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?
) : Animatable {
    override fun key(): String {
        return pickingID.toString()
    }

}