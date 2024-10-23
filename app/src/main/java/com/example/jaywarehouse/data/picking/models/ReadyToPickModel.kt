package com.example.jaywarehouse.data.picking.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReadyToPickModel(
    @SerializedName("rows")
    val rows: List<ReadyToPickRow>,
    @SerializedName("total")
    val total: Int
)

data class ReadyToPickRow(
    @SerializedName("Barcode")
    val barcode: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("Model")
    val model: String,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("ScanCount")
    val scanCount: Int?,
    @SerializedName("Time")
    val time: String,
    @SerializedName("Brand")
    val brand: String?,
    @SerializedName("Type")
    val type: String?,
    @SerializedName("Size")
    val size: String?,
    @SerializedName("Gender")
    val gender: String?
) : Serializable