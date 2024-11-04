package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingRow(
    @SerializedName("ReceivingID")
    val receivingID: Int,
    @SerializedName("CountedQuantity")
    val countedQuantity: Int,
    @SerializedName("ReceivingDate")
    val receivingDate: String,
    @SerializedName("ReceivingDetailCount")
    val receivingDetailCount: Int,
    @SerializedName("ReceivingDetailSumQuantity")
    val receivingDetailSumQuantity: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("SupplierFullName")
    val supplierFullName: String
) : Serializable