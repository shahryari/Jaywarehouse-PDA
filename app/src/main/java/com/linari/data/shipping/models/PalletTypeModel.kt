package com.linari.data.shipping.models

import com.linari.presentation.common.utils.Selectable
import com.google.gson.annotations.SerializedName

data class PalletTypeModel(
    @SerializedName("rows")val rows: List<PalletTypeRow>,
    @SerializedName("total")val total: Int
)

data class PalletTypeRow(
    @SerializedName("PalletTypeID")val palletTypeID: Int,
    @SerializedName("PalletTypeTitle")val palletTypeTitle: String
) : Selectable{
    override fun toString(): String {
        return palletTypeTitle
    }

    override fun string(): String {
        return palletTypeTitle
    }
}
