package com.linari.data.checking.models

import com.linari.presentation.common.utils.Selectable
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
) : Selectable {
    override fun toString(): String {
        return palletStatusTitle
    }

    override fun string(): String {
        return palletStatusTitle
    }
}