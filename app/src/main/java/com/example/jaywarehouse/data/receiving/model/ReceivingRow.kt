package com.example.jaywarehouse.data.receiving.model

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingRow(
    @SerializedName("Count")
    val count: Double?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("Date")
    val receivingDate: String,
    @SerializedName("ReceivingID")
    val receivingID: Int,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Int,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("SupplierFullName")
    val supplierFullName: String,
    @SerializedName("Total")
    val total: Double,
    @SerializedName("WarehouseName")
    val warehouseName: String
) : Serializable, Animatable{
    override fun key(): String {
        return receivingID.toString()
    }

}

