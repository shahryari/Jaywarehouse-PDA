package com.linari.data.packing.model
import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PackingModel(
    @SerializedName("rows")
    val rows: List<PackingRow>,
    @SerializedName("total")
    val total: Int
)

data class PackingRow(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerID")
    val customerID: Long,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("Date")
    val date: String,
    @SerializedName("ItemCount")
    val itemCount: Double,
    @SerializedName("PackingID")
    val packingID: Long,
    @SerializedName("PackingNumber")
    val packingNumber: String,
    @SerializedName("SumPackedQty")
    val sumPackedQty: Double?,
    @SerializedName("Time")
    val time: String
) : Serializable, Animatable {
    override fun key(): String {
        return packingID.toString()
    }

}