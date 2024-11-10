package com.example.jaywarehouse.data.picking.models

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
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductInventoryExpireDate")
    val productInventoryExpireDate: String,
    @SerializedName("ProductInventoryHistoryID")
    val productInventoryHistoryID: Int?,
    @SerializedName("ProductLocationActivityID")
    val productLocationActivityID: Int,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Int,
    @SerializedName("TypeofOrderAcquisition")
    val typeofOrderAcquisition: String,
    @SerializedName("WarehouseLocationCode")
    val warehouseLocationCode: String
)