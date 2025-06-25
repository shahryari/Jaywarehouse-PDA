package com.example.jaywarehouse.data.shipping.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ShippingPalletManifestListModel(
    @SerializedName("rows") val rows: List<ShippingPalletManifestRow>,
    @SerializedName("total") val total: Int,
)

data class ShippingPalletManifestRow(
    @SerializedName("PalletBarcode") val palletBarcode: String?,
    @SerializedName("PalletManifestID") val palletManifestId: Int,
    @SerializedName("CustomerName") val customerName: String?,
    @SerializedName("CustomerCode") val customerCode: String?,
    @SerializedName("Total") val total: Double?
) : Animatable {
    override fun key(): String {
        return palletManifestId.toString()
    }

}