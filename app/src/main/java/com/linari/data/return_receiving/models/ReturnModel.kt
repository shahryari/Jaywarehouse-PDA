package com.linari.data.return_receiving.models

import com.google.gson.annotations.SerializedName
import com.linari.presentation.common.composables.Animatable
import java.io.Serializable

data class ReturnModel (
    @SerializedName("rows")
    val rows: List<ReturnRow>,
    @SerializedName("total")
    val total: Int
)

data class ReturnRow(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("PartnerCode")
    val partnerCode: String?,
    @SerializedName("PartnerName")
    val partnerName: String?,
    @SerializedName("ReceivingDate")
    val receivingDate: String?,
    @SerializedName("ReceivingDetailCount")
    val receivingDetailCount: Double?,
    @SerializedName("ReceivingID")
    val receivingID: Long,
    @SerializedName("ReceivingNumber")
    val receivingNumber: String?,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?
) : Serializable, Animatable{
    override fun key(): String {
        return receivingID.toString()
    }

}

