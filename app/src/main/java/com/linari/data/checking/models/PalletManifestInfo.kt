package com.linari.data.checking.models

import com.google.gson.annotations.SerializedName


data class PalletManifestInfo(
    @SerializedName("HasPallet") val hasPallet: Boolean,
    @SerializedName("PalletStatusID") val palletStatusID: Long?,
    @SerializedName("PalletStatusTitle")val palletStatusTitle: String?,
    @SerializedName("PalletTypeID") val palletTypeID: Long?,
    @SerializedName("PalletTypeTitle") val palletTypeTitle: String?
)