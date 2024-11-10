package com.example.jaywarehouse.data.picking.models

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
    val b2BCustomer: Any?,
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Total")
    val total: Int,
    @SerializedName("Count")
    val count: Int
) : Serializable