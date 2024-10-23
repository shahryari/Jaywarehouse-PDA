package com.example.jaywarehouse.data.packing.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PackingModel(
    @SerializedName("rows")
    val rows: List<PackingRow>,
    @SerializedName("total")
    val total: Int
)

data class PackingRow(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Int,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("ItemCount")
    val itemCount: Int,
    @SerializedName("PackingID")
    val packingID: Int,
    @SerializedName("PackingNumber")
    val packingNumber: String,
    @SerializedName("SumPackedQty")
    val sumPackedQty: Int?,
    @SerializedName("Time")
    val time: String
) : Serializable