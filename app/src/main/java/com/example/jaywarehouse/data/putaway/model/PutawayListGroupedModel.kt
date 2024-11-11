package com.example.jaywarehouse.data.putaway.model

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
    val count: Int,
    @SerializedName("ReceiptID")
    val receiptID: Int,
    @SerializedName("ReceivingTypeID")
    val receivingTypeID: Int,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("SupplierFullName")
    val supplierFullName: String,
    @SerializedName("Total")
    val total: Int
) : Serializable
