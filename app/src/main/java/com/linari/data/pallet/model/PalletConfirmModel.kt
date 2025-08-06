package com.linari.data.pallet.model

import com.linari.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PalletConfirmModel(
    @SerializedName("rows")
    val rows: List<PalletConfirmRow>,
    @SerializedName("total")
    val total: Int
)


data class PalletConfirmRow(
    @SerializedName("B2BCustomer")
    val b2BCustomer: String?,
    @SerializedName("CustomerCode")
    val customerCode: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("PalletBarcode")
    val palletBarcode: String,
    @SerializedName("PalletManifestID")
    val palletManifestID: Int,
    @SerializedName("Total")
    val total: Double?
) : Animatable , Serializable{
    override fun key(): String {
        return palletManifestID.toString()
    }

}

