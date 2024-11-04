package com.example.jaywarehouse.data.putaway.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReadyToPutModel(
    @SerializedName("rows")
    val rows: List<ReadyToPutRow>,
    @SerializedName("total")
    val total: Int
)

data class ReadyToPutRow(
    @SerializedName("Barcode")
    val barcode: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("Model")
    val model: String,
    @SerializedName("PutCount")
    val putCount: Int,
    @SerializedName("BoxNumber")
    val boxNumber: String?,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("ReceivingDetailID")
    val receivingDetailID: Int,
    @SerializedName("ReceivingID")
    val receivingID: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("Time")
    val time: String
) : Serializable