package com.example.jaywarehouse.data.shipping.models
import com.example.jaywarehouse.presentation.common.composables.Animatable
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
    @SerializedName("CurrentStatusCode")
    val currentStatusCode: String?,
    @SerializedName("DriverFullName")
    val driverFullName: String?,
    @SerializedName("DriverTin")
    val driverTin: String,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("ShippingNumber")
    val shippingNumber: String,
    @SerializedName("TrailerNumber")
    val trailerNumber: String?
) : Serializable, Animatable {
    override fun key(): String {
        return shippingID.toString()
    }

}