package com.linari.data.auth.models

import com.linari.presentation.common.utils.MainItems
import com.google.gson.annotations.SerializedName


data class AccessPermissionModel(

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
    val hasPickingBD: Boolean
) {
    fun checkAccess(mainItems: MainItems) : Boolean{
        return when(mainItems){
            MainItems.Receiving -> hasCount
            MainItems.ManualPutaway -> hasPutaway
            MainItems.ReturnReceiving -> hasReturnReceiving
            MainItems.Picking -> hasPicking
            MainItems.Checking -> hasChecking
            MainItems.PalletConfirm -> hasPalletConfirm
            MainItems.Loading -> hasLoading
            MainItems.ShippingTruck -> hasShipping
            MainItems.Inventory -> hasInventory
            MainItems.Transfer -> hasTransfer
            MainItems.CycleCount -> hasCycleCount
            MainItems.RS -> hasRS
            MainItems.PickingBD -> hasPickingBD
        }
    }
}