package com.linari.data.rs.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.composables.Animatable

data class WaybillInfoModel(
    @SerializedName("rows")
    val rows: List<WaybillInfoRow>,
    @SerializedName("total")
    val total: Int
)
data class WaybillInfoRow(
    @SerializedName("WaybillInfoID")
    val waybillInfoID: Int,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("DriverTin")
    val driverTin: String?,
    @SerializedName("DriverFullName")
    val driverFullName: String?,
    @SerializedName("CarNumber")
    val carNumber: String?,
    @SerializedName("TrailerNumber")
    val trailerNumber: String?,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("WaybillNumber")
    val waybillNumber: String?,
) : Animatable {
    override fun key(): String {
        return waybillInfoID.toString()
    }
}