package com.linari.data.return_receiving.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.utils.Selectable

data class OwnerInfoModel(
    @SerializedName("rows") val rows: List<OwnerInfoRow>,
    @SerializedName("total") val total: Int
)

data class OwnerInfoRow(
    @SerializedName("OwnerInfoID") val ownerInfoID: Long,
    @SerializedName("OwnerName") val ownerName: String,
    @SerializedName("OwnerCode") val ownerCode: String,
) : Selectable {
    override fun string(): String {
        return "$ownerName($ownerCode)"
    }

}