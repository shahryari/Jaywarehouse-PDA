package com.example.jaywarehouse.data.checking.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CheckingListGroupedModel(
    @SerializedName("rows")
    val rows: List<CheckingListGroupedRow>,
    @SerializedName("total")
    val total: Int
)

data class CheckingListGroupedRow(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("GroupedProductCount")
    val groupedProductCount: Int
) : Serializable, Animatable {
    override fun key(): String {
        return customerID.toString()
    }
}