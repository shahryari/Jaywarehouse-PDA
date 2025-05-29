package com.example.jaywarehouse.data.rs.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class PODInvoiceModel(
    @SerializedName("rows") val rows: List<PODInvoiceRow>,
    @SerializedName("total") val total: Int
)


data class PODInvoiceRow(
    @SerializedName("Address")
    val address: String?,
    @SerializedName("AddressInfoNumber")
    val addressInfoNumber: String?,
    @SerializedName("CarNumber")
    val carNumber: String?,
    @SerializedName("CustomerFullName")
    val customerFullName: String?,
    @SerializedName("DriverFullName")
    val driverFullName: String?,
    @SerializedName("DriverTin")
    val driverTin: String?,
    @SerializedName("PODInvoiceID")
    val pODInvoiceID: Int,
    @SerializedName("PODInvoiceNumber")
    val pODInvoiceNumber: String?,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("ShippingOrderReferenceNumbers")
    val shippingOrderReferenceNumbers: String?,
    @SerializedName("ShippingOrderTypeID")
    val shippingOrderTypeID: Int?,
    @SerializedName("ShippingOrderTypeTitle")
    val shippingOrderTypeTitle: String?,
    @SerializedName("ShippingTypeID")
    val shippingTypeID: Int?,
    @SerializedName("ShippingTypeTitle")
    val shippingTypeTitle: String?,
    @SerializedName("TrailerNumber")
    val trailerNumber: String?,
    @SerializedName("WaybillNumber")
    val waybillNumber: String?
) : Animatable {
    override fun key(): String {
        return "${shippingID}_$pODInvoiceID"
    }
}
