package com.example.jaywarehouse.data.shipping.models

import androidx.compose.material3.ListItemColors
import com.google.gson.annotations.SerializedName

data class PalletInShippingModel(
    @SerializedName("rows") val rows: List<PalletInShippingRow>,
    @SerializedName("total") val total: Int
)


data class PalletInShippingRow(
    @SerializedName("CustomerID")
    val customerID: String,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("PalletQuantity")
    val palletQuantity: Int,
    @SerializedName("PalletTypeID")
    val palletTypeID: Int,
    @SerializedName("PalletTypeTitle")
    val palletTypeTitle: String?,
    @SerializedName("ShippingID")
    val shippingID: Int,
    @SerializedName("EntityState")
    val entityState: String?
)