package com.example.jaywarehouse.data.transfer.models

import com.google.gson.annotations.SerializedName



data class ProductStatusModel(
    @SerializedName("rows") val rows: List<ProductStatusRow>,
    @SerializedName("total")val total: Int
)
data class ProductStatusRow(
    @SerializedName("QuiddityTypeID")
    val quiddityTypeId: Int,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String
) {
    override fun toString(): String {
        return quiddityTypeTitle
    }
}