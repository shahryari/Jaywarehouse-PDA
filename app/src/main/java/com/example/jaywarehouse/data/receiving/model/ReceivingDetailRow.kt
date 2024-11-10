package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingDetailRow(
    @SerializedName("CountQuantity")
    val countQuantity: Int?,
    @SerializedName("OwnerFullName")
    val ownerFullName: String,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("QuiddityTypeID")
    val quiddityTypeID: Int,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Int,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String?,
    @SerializedName("ReceivingWorkerTaskID")
    val receivingWorkerTaskID: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("SupplierFullName")
    val supplierFullName: String
) : Serializable