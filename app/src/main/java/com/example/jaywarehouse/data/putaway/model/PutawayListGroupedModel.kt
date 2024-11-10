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
    @SerializedName("ReceiptID")
    val receiptId: Int,
    @SerializedName("ReceivingTypeID")
    val receivingTypeId: Int,
    @SerializedName("PutCount")
    val putCount: Int,
    @SerializedName("OnPuting")
    val onPuting: Int,
    @SerializedName("ReferenceNumber")
    val referenceNumber: String,
    @SerializedName("SupplierFullName")
    val supplierFullName: String
) : Serializable