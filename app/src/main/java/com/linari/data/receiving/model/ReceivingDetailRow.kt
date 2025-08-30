package com.linari.data.receiving.model

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingDetailRow(
    @SerializedName("CountQuantity")
    val countQuantity: Double?,
    @SerializedName("OwnerFullName")
    val ownerFullName: String?,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String?,
    @SerializedName("ProductCode")
    val productCode: String,
    @SerializedName("ProductName")
    val productName: String,
    @SerializedName("Quantity")
    val quantity: Double,
    @SerializedName("QuiddityTypeID")
    val quiddityTypeID: Long?,
    @SerializedName("QuiddityTypeTitle")
    val quiddityTypeTitle: String?,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Long,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String?,
    @SerializedName("ReceivingWorkerTaskID")
    val receivingWorkerTaskID: Long,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("SupplierFullName")
    val supplierFullName: String?,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("ExpDate")
    val expireDate: String?,
    @SerializedName("IsWeight")
    val isWeight: Boolean?,
    @SerializedName("Pcb")
    val pcb: Double?
) : Serializable, Animatable {
    override fun key(): String {
        return receivingWorkerTaskID.toString()
    }

}