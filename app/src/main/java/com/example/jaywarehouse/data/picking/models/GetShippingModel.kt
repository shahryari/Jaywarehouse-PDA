package com.example.jaywarehouse.data.picking.models
import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName


data class GetShippingModel(
    @SerializedName("rows")
    val rows: List<GetShippingRow>,
    @SerializedName("total")
    val total: Int
)

data class GetShippingRow(
    @SerializedName("CarNumber")
    val carNumber: String,
    @SerializedName("CurrentStatusCode")
    val currentStatusCode: String?,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("DriverFullName")
    val driverFullName: String,
    @SerializedName("DriverTin")
    val driverTin: String,
    @SerializedName("PalletManifests")
    val palletManifests: List<PalletManifest>,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("ShippingNumber")
    val shippingNumber: String,
    @SerializedName("ShippingStatus")
    val shippingStatus: Int,
    @SerializedName("Time")
    val time: String,
    @SerializedName("TrailerNumber")
    val trailerNumber: String,
    @SerializedName("WarehouseID")
    val warehouseID: Int
)



data class PalletManifest(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("PalletBarcode")
    val palletBarcode: String?,
    @SerializedName("PalletManifestID")
    val palletManifestID: Int,
    @SerializedName("Total")
    val total: Double?,
    @SerializedName("WarehouseID")
    val warehouseID: Int
) : Animatable {
    override fun key(): String {
        return palletManifestID.toString()
    }

}
