package com.example.jaywarehouse.data.shipping.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ShippingDetailListOfPalletModel(
    @SerializedName("rows") val rows: List<ShippingDetailListOfPalletRow>,
    @SerializedName("total") val total: Int
)

data class ShippingDetailListOfPalletRow(
    @SerializedName("ShippingOrderDetailID") val shippingOrderDetailID: Int,
    @SerializedName("PalletManifestID") val palletManifestID: Int,
    @SerializedName("ProductCode") val productCode: String?,
    @SerializedName("ProductName") val productName: String?,
    @SerializedName("ProductBarcodeNumber") val productBarcodeNumber: String?,
    @SerializedName("Quantity") val quantity: Double?
) : Animatable {
    override fun key(): String {
        return shippingOrderDetailID.toString()
    }

}