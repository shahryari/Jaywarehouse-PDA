package com.linari.data.picking.models

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PurchaseOrderDetailListBDModel(
    @SerializedName("rows") val rows: List<PurchaseOrderDetailListBDRow>,
    @SerializedName("total") val total: Int
)


data class PurchaseOrderDetailListBDRow(
    @SerializedName("PurchaseOrderDetailID")
    val purchaseOrderDetailID: Int,
    @SerializedName("ProductCode")
    val productCode: String?,
    @SerializedName("ProductName")
    val productName: String?,
    @SerializedName("ProductBarcodeNumber")
    val barcodeNumber: String?,
    @SerializedName("Quantity")
    val quantity: Double?,
    @SerializedName("PCB")
    val pcb: Int?,
    @SerializedName("ProductID")
    val productID: Int?,
    @SerializedName("SumReceiptQuantity")
    val sumReceiptQuantity: Double?,
    @SerializedName("SumPickingQty")
    val sumPickingQty: Double?,
    @SerializedName("QuantityDifferencePodandPicks")
    val quantityDifferencePodandPIcks: Double?
) : Animatable, Serializable {
    override fun key(): String {
        return purchaseOrderDetailID.toString()
    }

}