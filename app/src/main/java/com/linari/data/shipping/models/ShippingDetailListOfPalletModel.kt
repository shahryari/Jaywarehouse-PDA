package com.linari.data.shipping.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ShippingDetailListOfPalletModel(
    @SerializedName("rows") val rows: List<ShippingDetailListOfPalletRow>,
    @SerializedName("total") val total: Int
)

data class ShippingDetailListOfPalletRow(
    @SerializedName("IsWeight")
    val isWeight: Boolean?,
    @SerializedName("ProductBarcodeNumber")
    val productBarcodeNumber: String?,
    @SerializedName("ProductCode")
    val productCode: String?,
    @SerializedName("ProductName")
    val productName: String?,
    @SerializedName("Quantity")
    val quantity: Double?,
    @SerializedName("ExpDate")
    val expireDate: String?,
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("ReferenceNumberLPO")
    val referenceNumberLPO: String?,
    @SerializedName("ReferenceNumberPO")
    val referenceNumberPO: String?,
    @SerializedName("ShippingDetailID")
    val shippingDetailID: Int
) : Animatable {
    override fun key(): String {
        return shippingDetailID.toString()+productBarcodeNumber
    }

}