package com.linari.data.shipping.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ShippingPalletManifestListModel(
    @SerializedName("rows") val rows: List<ShippingPalletManifestRow>,
    @SerializedName("total") val total: Int,
)

data class ShippingPalletManifestRow(
    @SerializedName("PalletBarcode") val palletBarcode: String?,
    @SerializedName("PalletManifestID") val palletManifestId: Long,
    @SerializedName("PartnerName") val customerName: String?,
    @SerializedName("PartnerCode") val customerCode: String?,
    @SerializedName("Total") val total: Double?
) : Animatable {
    override fun key(): String {
        return palletManifestId.toString()
    }

}