package com.linari.data.putaway.model

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PutawayListGroupedModel(
    @SerializedName("rows")
    val rows: List<PutawayListGroupedRow>,
    @SerializedName("total")
    val total: Int
)


data class PutawayListGroupedRow(
    @SerializedName("Count")
    val count: Double?,
    @SerializedName("ReceiptID")
    val receiptID: Long,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Long?,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String?,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String?,
    @SerializedName("SupplierFullName")
    val supplierFullName: String?,
    @SerializedName("Total")
    val total: Double
) : Serializable, Animatable{
    override fun key(): String {
        return receiptID.toString()
    }

}
