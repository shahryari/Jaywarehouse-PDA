package com.linari.data.picking.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class PickingListBDModel(
    @SerializedName("rows")
    val rows: List<PickingListBDRow>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("purchaseOrderDetail")
    val purchaseOrderDetail: PurchaseOrderDetailListBDRow?
)

data class PickingListBDRow(
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("PickingID")
    val pickingID: Int,
    @SerializedName("PurchaseOrderDetailID")
    val purchaseOrderDetailID: Int,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("Quantity")
    val splittedQuantity: Double?
) : Animatable {
    override fun key(): String {
        return pickingID.toString()
    }

}