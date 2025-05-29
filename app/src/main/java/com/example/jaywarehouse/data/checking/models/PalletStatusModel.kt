package com.example.jaywarehouse.data.checking.models

import com.google.gson.annotations.SerializedName

data class PalletStatusModel(
    @SerializedName("rows")
    val rows: List<PalletStatusRow>,
    @SerializedName("total")
    val total: Int
)


data class PalletStatusRow(
    @SerializedName("PalletStatusID")
    val palletStatusID: Int,
    @SerializedName("PalletStatusTitle")
    val palletStatusTitle: String
) {
    override fun toString(): String {
        return palletStatusTitle
    }
}