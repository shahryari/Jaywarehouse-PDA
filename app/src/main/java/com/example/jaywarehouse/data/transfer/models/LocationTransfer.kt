package com.example.jaywarehouse.data.transfer.models
import com.google.gson.annotations.SerializedName

data class LocationTransferModel(
    @SerializedName("rows")
    val rows: List<LocationTransferRow>,
    @SerializedName("total")
    val total: Int
)

data class LocationTransferRow(
    @SerializedName("Barcode")
    val barcode: String,
    @SerializedName("BoxNumber")
    val boxNumber: String?,
    @SerializedName("Date")
    val date: String?,
    @SerializedName("LocationCode")
    val locationCode: String?,
    @SerializedName("LocationTransferID")
    val locationTransferID: Int,
    @SerializedName("LocationTransferNumber")
    val locationTransferNumber: String?,
    @SerializedName("Model")
    val model: String,
    @SerializedName("Time")
    val time: String
)
