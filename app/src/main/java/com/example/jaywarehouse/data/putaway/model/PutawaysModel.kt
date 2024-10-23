package com.example.jaywarehouse.data.putaway.model
import com.google.gson.annotations.SerializedName


data class PutawaysModel(
    @SerializedName("rows")
    val rows: List<PutawaysRow>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("ReceivingDetail")
    val putRow: ReadyToPutRow?
)

data class PutawaysRow(
    @SerializedName("Date")
    val date: String,
    @SerializedName("PutawayScanID")
    val putawayScanID: Int,
    @SerializedName("ReceivingDetailID")
    val receivingDetailID: Int,
    @SerializedName("Quantity")
    val quantity: Int,
    @SerializedName("Time")
    val time: String
)