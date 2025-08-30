package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.utils.Selectable


data class ShippingCustomerModel(
    @SerializedName("rows") val rows: List<ShippingCustomerRow>,
    @SerializedName("total") val total: Int
)

data class ShippingCustomerRow(
    @SerializedName("CustomerID") val customerID: Long,
    @SerializedName("CustomerName") val customerName: String,
    @SerializedName("CustomerCode") val customerCode: String
) : Selectable{
    override fun toString(): String {
        return "${customerName.trim().trimIndent()}($customerCode)"
    }

    override fun string(): String {
        return "${customerName.trim().trimIndent()}($customerCode)"
    }
}