package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingRow(
    @SerializedName("Count")
    val count: Int?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("ReceivingDate")
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
    val total: Int,
    @SerializedName("WarehouseName")
    val warehouseName: String
) : Serializable

