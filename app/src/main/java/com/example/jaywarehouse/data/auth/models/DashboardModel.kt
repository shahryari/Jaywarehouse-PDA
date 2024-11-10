package com.example.jaywarehouse.data.auth.models


import com.example.jaywarehouse.presentation.common.utils.MainItems
import com.google.gson.annotations.SerializedName

data class DashboardModel(
    @SerializedName("CheckingCount")
    val checkingCount: Int,
    @SerializedName("LoadingCount")
    val loadingCount: Int,
    @SerializedName("PalletConfirmCount")
    val palletConfirmCount: Int,
    @SerializedName("PickingCount")
    val pickingCount: Int,
    @SerializedName("PutawayCount")
    val putawayCount: Int,
    @SerializedName("PutawayManualCount")
    val putawayManualCount: Int,
    @SerializedName("ReceivingCount")
    val receivingCount: Int,
    @SerializedName("ReturnReceivingCount")
    val returnReceivingCount: Int,
    @SerializedName("ShippingTruckCount")
    val shippingTruckCount: Int
) {
    fun getCount(item: MainItems): Int? {
        return when(item){
            MainItems.Receiving -> receivingCount
            MainItems.Checking -> checkingCount
            MainItems.Loading -> loadingCount
            MainItems.Picking -> pickingCount
            MainItems.Putaway -> putawayCount
            MainItems.ManualPutaway -> putawayManualCount
            MainItems.ReturnReceiving -> returnReceivingCount
            MainItems.ShippingTruck -> shippingTruckCount
            MainItems.PalletConfirm -> palletConfirmCount
            else -> null
        }
    }
}

