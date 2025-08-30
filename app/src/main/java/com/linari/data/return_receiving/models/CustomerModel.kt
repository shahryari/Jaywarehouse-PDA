package com.linari.data.return_receiving.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.utils.Selectable

data class CustomerModel(
    @SerializedName("rows") val rows: List<CustomerRow>,
    @SerializedName("total") val total: Int,
)

data class CustomerRow(
    @SerializedName("PartnerID")
    val partnerID: Long,
    @SerializedName("PartnerName")
    val partnerName: String,
    @SerializedName("PartnerCode")
    val partnerCode: String
) : Selectable {
    override fun toString(): String {
        return "${partnerName.trim().trimIndent()}($partnerCode)"
    }

    override fun string(): String {
        return "${partnerName.trim().trimIndent()}($partnerCode)"
    }

}