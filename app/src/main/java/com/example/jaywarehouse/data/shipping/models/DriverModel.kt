package com.example.jaywarehouse.data.shipping.models

import com.google.gson.annotations.SerializedName

data class DriverModel(
    @SerializedName("CarNumber")
    val carNumber: String,
    @SerializedName("DriverTin")
    val driverTin: String,
    @SerializedName("FullName")
    val fullName: String,
    @SerializedName("TrailerNumber")
    val trailerNumber: String
)