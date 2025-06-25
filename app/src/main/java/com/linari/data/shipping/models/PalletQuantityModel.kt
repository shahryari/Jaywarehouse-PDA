package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName

data class PalletQuantityModel(
    @SerializedName("CustomerID")
    val customerID: String,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("PalletQuantity")
    val palletQuantity: String,
    @SerializedName("PalletTypeID")
    val palletTypeID: String,
    @SerializedName("PalletTypeTitle")
    val palletTypeTitle: String,
    @SerializedName("ShippingID")
    val shippingID: String
)