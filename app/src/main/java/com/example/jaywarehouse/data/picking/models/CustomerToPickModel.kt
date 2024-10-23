package com.example.jaywarehouse.data.picking.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CustomerToPickModel(
    @SerializedName("rows")
    val rows: List<CustomerToPickRow>,
    @SerializedName("total")
    val total: Int
)

data class CustomerToPickRow(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("SumPickedQty")
    val sumPickedQty: Int?,
    @SerializedName("SumQuantity")
    val sumQuantity: Int
) : Serializable