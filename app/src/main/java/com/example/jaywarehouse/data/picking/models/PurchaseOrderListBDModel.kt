package com.example.jaywarehouse.data.picking.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PurchaseOrderListBDModel(
    @SerializedName("rows") val rows: List<PurchaseOrderListBDRow>,
    @SerializedName("total") val total: Int
)

data class PurchaseOrderListBDRow(
    @SerializedName("ReferenceNumber") val referenceNumber: String?,
    @SerializedName("SupplierID") val supplierID: String?,
    @SerializedName("SupplierName") val supplierName: String?,
    @SerializedName("SupplierCode") val supplierCode: String?,
    @SerializedName("PurchaseOrderID") val purchaseOrderID: Int,
    @SerializedName("PurchaseOrderDate") val purchaseOrderDate: String?
) : Animatable, Serializable{
    override fun key(): String {
        return purchaseOrderID.toString()
    }

}