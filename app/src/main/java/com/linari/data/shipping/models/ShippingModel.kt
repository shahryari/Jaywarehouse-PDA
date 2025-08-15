package com.linari.data.shipping.models
import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ShippingModel(
    @SerializedName("rows")
    val rows: List<ShippingRow>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("WarehouseID")
    val warehouseID: String?
)


data class ShippingRow(
    @SerializedName("CarNumber")
    val carNumber: String?,
    @SerializedName("ShippingStatus")
    val shippingStatus: Int?,
    @SerializedName("DriverFullName")
    val driverFullName: String?,
    @SerializedName("DriverTin")
    val driverTin: String?,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("ShippingNumber")
    val shippingNumber: String?,
    @SerializedName("TrailerNumber")
    val trailerNumber: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("PalletCount")
    val palletCount: Int?,
    @SerializedName("SumPalletQty")
    val sumPalletQty: Double?,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("WarehouseID")
    val warehouseID: String?,
    @SerializedName("Date")
    val date: String?,
    @SerializedName("Time")
    val time: String?
) : Serializable, Animatable {
    override fun key(): String {
        return shippingID.toString()
    }

}