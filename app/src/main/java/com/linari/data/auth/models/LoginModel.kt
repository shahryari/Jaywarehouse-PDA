package com.linari.data.auth.models

import com.linari.presentation.pallet.contracts.PalletConfirmContract
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("Username") val username: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("TokenID") val tokenID: String,
    @SerializedName("Message") val message: String? = null,
    @SerializedName("HasChecking")
    val hasChecking: Boolean,
    @SerializedName("UserID")
    val userID: String,
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
    @SerializedName("HasWaybill")
    val hasWaybill: Boolean,
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
    val hasWaste: Boolean,
    @SerializedName("HasPickCancel")
    val hasPickCancel: Boolean
)

