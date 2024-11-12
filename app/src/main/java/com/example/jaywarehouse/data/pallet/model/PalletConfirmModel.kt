package com.example.jaywarehouse.data.pallet.model

import com.google.gson.annotations.SerializedName

data class PalletConfirmModel(
    @SerializedName("rows")
    val rows: List<PalletConfirmRow>,
    @SerializedName("total")
    val total: Int
)

data class PalletConfirmRow(
    @SerializedName("PalletBarcode")
    val palletBarcode: String,
    @SerializedName("PalletManifestID")
    val palletManifestID: Int,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("Total")
    val total: Int?
)