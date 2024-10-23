package com.example.jaywarehouse.data.shipping.models
import com.google.gson.annotations.SerializedName


class DriverModel : ArrayList<DriverModelItem>()

data class DriverModelItem(
    @SerializedName("DriverID")
    val driverId: Int,
    @SerializedName("CarNumber")
    val carNumber: String,
    @SerializedName("DriverName")
    val driverName: String,
    @SerializedName("DriverTin")
    val driverTin: String
) {
    override fun toString(): String {
        return driverName
    }
}