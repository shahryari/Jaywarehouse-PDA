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
    val customerID: Long,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("ExpDate")
    val expireDate: String?,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("PickingID")
    val pickingID: Long,
    @SerializedName("ShippingOrderID")
    val shippingOrderID: Long,
    @SerializedName("ProductCode")
    val productCode: String?,
    @SerializedName("ProductName")
    val productName: String?,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("IsWeight")
    val isWeight: Boolean?,
    @SerializedName("PurchaseOrderDetailID")
    val purchaseOrderDetailID: Long,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Long,
    @SerializedName("TypeofOrderAcquisition")
    val typeofOrderAcquisition: String?,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String?
) : Animatable {
    override fun key(): String {
        return pickingID.toString()
    }

}