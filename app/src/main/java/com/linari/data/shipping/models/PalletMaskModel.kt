package com.linari.data.shipping.models

import com.google.gson.annotations.SerializedName


data class PalletMaskModel(
    @SerializedName("PalletMaskAbbreviation")
    val palletMaskAbbreviation: String
)
