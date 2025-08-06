package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName



data class CustomerPalletIsNotInShippingModel(
    @SerializedName("rows")
    val rows: List<CustomerPalletIsNotInShippingRow>
)

data class CustomerPalletIsNotInShippingRow(
    @SerializedName("PalletBarcode")
    val palletBarcodes: String,
    @SerializedName("PartnerName")
    val customerName: String
)
