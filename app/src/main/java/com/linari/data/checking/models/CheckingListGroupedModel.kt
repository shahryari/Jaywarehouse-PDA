package com.linari.data.checking.models

import com.linari.presentation.common.composables.Animatable
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
    @SerializedName("CustomerTypeTitle")
    val customerTypeTitle: String?,
    @SerializedName("Count")
    val count: Double,
    @SerializedName("SumQuantity")
    val sumQuantity: Double?
) : Serializable, Animatable {
    override fun key(): String {
        return customerID.toString()
    }
}