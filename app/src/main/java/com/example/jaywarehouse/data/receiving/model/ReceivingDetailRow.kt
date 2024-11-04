package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReceivingDetailRow(
    @SerializedName("ReceivingID") val receivingID: Int,
    @SerializedName("ReferenceNumber") val referenceNumber: String?,
    @SerializedName("Quantity") val quantity: Int,
    @SerializedName("Barcode") val barcode: String,
    @SerializedName("Model") val model: String,
    @SerializedName("ScanCount") val scanCount: Int,
    @SerializedName("CreatedOn") val createdOn: String
) : Serializable