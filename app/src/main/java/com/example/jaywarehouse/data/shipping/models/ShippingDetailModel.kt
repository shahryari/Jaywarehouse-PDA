package com.example.jaywarehouse.data.shipping.models
import com.google.gson.annotations.SerializedName


data class ShippingDetailModel(
    @SerializedName("rows")
    val rows: List<ShippingDetailRow>,
    @SerializedName("Shipping")
    val shipping: ShippingRow,
    @SerializedName("total")
    val total: Int
)

data class ShippingDetailRow(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int?,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("ItemCount")
    val itemCount: Int?,
    @SerializedName("PackingID")
    val packingID: Int,
    @SerializedName("PackingNumber")
    val packingNumber: String,
    @SerializedName("SumPackedQty")
    val sumPackedQty: Int?,
    @SerializedName("Time")
    val time: String
)
