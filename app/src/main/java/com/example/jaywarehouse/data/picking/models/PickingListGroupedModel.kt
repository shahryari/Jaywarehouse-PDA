package com.example.jaywarehouse.data.picking.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PickingListGroupedModel(
    @SerializedName("rows")
    val rows: List<PickingListGroupedRow>,
    @SerializedName("total")
    val total: Int
)

data class PickingListGroupedRow(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Total")
    val total: Double,
    @SerializedName("Count")
    val count: Double,
    @SerializedName("ShippingOrderTypeTitle")
    val typeTitle: String?
) : Serializable, Animatable {
    override fun key(): String {
        return customerID.toString()
    }

}