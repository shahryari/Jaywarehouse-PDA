package com.example.jaywarehouse.data.shipping.models

import com.google.gson.annotations.SerializedName

data class PalletTypeModel(
    @SerializedName("rows")val rows: List<PalletTypeRow>,
    @SerializedName("total")val total: Int
)

data class PalletTypeRow(
    @SerializedName("PalletTypeID")val palletTypeID: Int,
    @SerializedName("PalletTypeTitle")val palletTypeTitle: String
) {
    override fun toString(): String {
        return palletTypeTitle
    }
}
