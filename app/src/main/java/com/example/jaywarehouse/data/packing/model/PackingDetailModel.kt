package com.example.jaywarehouse.data.packing.model
import com.example.jaywarehouse.presentation.common.composables.Animatable
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
    val packingDetailID: Int,
    @SerializedName("PackingID")
    val packingID: Int,
    @SerializedName("Quantity")
    val quantity: Double
) : Animatable {
    override fun key(): String {
        return packingDetailID.toString()
    }
}