package com.example.jaywarehouse.data.auth.models

import com.example.jaywarehouse.presentation.pallet.PalletConfirmContract
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("Username") val username: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("TokenID") val tokenID: String,
    @SerializedName("Message") val message: String? = null,
    @SerializedName("HasChecking")
    val hasChecking: Boolean,
    @SerializedName("HasCount")
    val hasCount: Boolean,
    @SerializedName("HasCycleCount")
    val hasCycleCount: Boolean,
    @SerializedName("HasInventory")
    val hasInventory: Boolean,
    @SerializedName("HasLoading")
    val hasLoading: Boolean,
    @SerializedName("HasPalletConfirm")
    val hasPalletConfirm: Boolean,
    @SerializedName("HasPicking")
    val hasPicking: Boolean,
    @SerializedName("HasPutaway")
    val hasPutaway: Boolean,
    @SerializedName("HasRS")
    val hasRS: Boolean,
    @SerializedName("HasReturnReceiving")
    val hasReturnReceiving: Boolean,
    @SerializedName("HasShipping")
    val hasShipping: Boolean,
    @SerializedName("HasTransfer")
    val hasTransfer: Boolean,
    @SerializedName("HasPickingBD")
    val hasPickingBD: Boolean,
    @SerializedName("Warehouse")
    val warehouse: WarehouseModel?,
    @SerializedName("HasModifyPickQty")
    val hasModifyPickQty: Boolean,
    @SerializedName("HasWaste")
    val hasWaste: Boolean
)

