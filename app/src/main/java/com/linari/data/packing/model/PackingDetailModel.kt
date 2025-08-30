package com.linari.data.packing.model
import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName


data class PackingDetailModel(
    @SerializedName("Packing")
    val packing: PackingRow,
    @SerializedName("rows")
    val rows: List<PackingDetailRow>,
    @SerializedName("total")
    val total: Int
)

data class PackingDetailRow(
    @SerializedName("Barcode")
    val barcode: String,
    @SerializedName("Model")
    val model: String,
    @SerializedName("PackingDetailID")
    val packingDetailID: Long,
    @SerializedName("PackingID")
    val packingID: Long,
    @SerializedName("Quantity")
    val quantity: Double
) : Animatable {
    override fun key(): String {
        return packingDetailID.toString()
    }
}