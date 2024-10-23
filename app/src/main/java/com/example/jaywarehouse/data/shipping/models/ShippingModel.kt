package com.example.jaywarehouse.data.shipping.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ShippingModel(
    @SerializedName("rows")
    val rows: List<ShippingRow>,
    @SerializedName("total")
    val total: Int
)

data class ShippingRow(
    @SerializedName("CarNumber")
    val carNumber: String?,
    @SerializedName("Date")
    val date: String,
    @SerializedName("DriverName")
    val driverName: String?,
    @SerializedName("DriverTin")
    val driverTin: String?,
    @SerializedName("SumQuantity")
    val itemCount: Int?,
    @SerializedName("PackCount")
    val scanCount: Int?,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("ShippingNumber")
    val shippingNumber: String,
    @SerializedName("Time")
    val time: String
) : Serializable