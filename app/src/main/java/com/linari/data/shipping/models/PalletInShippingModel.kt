package com.linari.data.shipping.models

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
    val customerName: String?,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("PalletQuantity")
    val palletQuantity: Int,
    @SerializedName("PalletTypeID")
    val palletTypeID: Long,
    @SerializedName("PalletTypeTitle")
    val palletTypeTitle: String?,
    @SerializedName("PalletStatusID")
    val palletStatusID: Long?,
    @SerializedName("PalletStatusTitle")
    val palletStatusTitle: String?,
    @SerializedName("ShippingID")
    val shippingID: Long,
    @SerializedName("ShippingPalletID")
    val shippingPalletID: Long,
)