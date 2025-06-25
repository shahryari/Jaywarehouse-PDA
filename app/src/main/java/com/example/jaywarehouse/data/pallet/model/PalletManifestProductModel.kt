package com.example.jaywarehouse.data.pallet.model

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class PalletManifestProductModel(
    @SerializedName("rows") val rows: List<PalletManifestProductRow>,
    @SerializedName("total") val total: Int
)

data class PalletManifestProductRow(
    @SerializedName("ProductCode") val productCode: String?,
    @SerializedName("ProductName") val productName: String?,
    @SerializedName("Quantity") val quantity: Double?,
    @SerializedName("ProductBarcodeNumber") val barcode: String?,
    @SerializedName("CheckQuantity") val checkQuantity: Double?,
    @SerializedName("CheckingID") val checkingID: Int,
    @SerializedName("ReferenceNumberLPO") val referenceNumberLPO : String?,
    @SerializedName("ReferenceNumberPO") val referenceNumberPO: String?
) : Animatable {
    override fun key(): String {
        return "$checkingID"
    }

}