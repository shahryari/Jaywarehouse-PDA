package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName


data class CustomerModel(
    @SerializedName("rows") val rows: List<CustomerRow>,
    @SerializedName("total") val total: Int
)

data class CustomerRow(
    @SerializedName("CustomerID") val customerID: Int,
    @SerializedName("CustomerName") val customerName: String,
    @SerializedName("CustomerCode") val customerCode: String
) {
    override fun toString(): String {
        return "${customerName.trim().trimIndent()}($customerCode)"
    }
}