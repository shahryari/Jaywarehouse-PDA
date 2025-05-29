package com.example.jaywarehouse.data.putaway.model

import com.example.jaywarehouse.presentation.common.composables.Animatable
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
    val receiptID: Int,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Int?,
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
