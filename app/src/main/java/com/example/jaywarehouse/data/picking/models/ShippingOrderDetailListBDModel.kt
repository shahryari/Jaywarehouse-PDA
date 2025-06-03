package com.example.jaywarehouse.data.picking.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ShippingOrderDetailListBDModel(
    @SerializedName("rows")
    val rows: List<ShippingOrderDetailListBDRow>,
    @SerializedName("total")
    val total: Int
)

data class ShippingOrderDetailListBDRow(
    @SerializedName("ShippingOrderDetailID")
    val shippingOrderDetailID: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("SplittedQuantity")
    val splittedQuantity: Double?
) : Animatable {
    override fun key(): String {
        return shippingOrderDetailID.toString()
    }

}