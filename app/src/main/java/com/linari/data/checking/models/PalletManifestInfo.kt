package com.linari.data.checking.models

import com.google.gson.annotations.SerializedName


data class PalletManifestInfo(
    @SerializedName("HasPallet") val hasPallet: Boolean,
    @SerializedName("PalletStatusID") val palletStatusID: Int?,
    @SerializedName("PalletStatusTitle")val palletStatusTitle: String?,
    @SerializedName("PalletTypeID") val palletTypeID: Int?,
    @SerializedName("PalletTypeTitle") val palletTypeTitle: String?
)