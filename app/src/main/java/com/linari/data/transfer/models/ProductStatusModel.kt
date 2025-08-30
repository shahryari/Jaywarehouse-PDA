package com.linari.data.transfer.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.utils.Selectable


data class ProductStatusModel(
    @SerializedName("rows") val rows: List<ProductStatusRow>,
    @SerializedName("total")val total: Int
)
data class ProductStatusRow(
    @SerializedName("QuiddityTypeID")
    val quiddityTypeId: Long,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String
) : Selectable{
    override fun toString(): String {
        return quiddityTypeTitle
    }

    override fun string(): String {
        return quiddityTypeTitle
    }
}