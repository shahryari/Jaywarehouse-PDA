package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.utils.Selectable


data class CustomerModel(
    @SerializedName("rows") val rows: List<CustomerRow>,
    @SerializedName("total") val total: Int
)

data class CustomerRow(
    @SerializedName("CustomerID") val customerID: Int,
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