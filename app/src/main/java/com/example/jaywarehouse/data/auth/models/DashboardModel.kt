package com.example.jaywarehouse.data.auth.models


import com.example.jaywarehouse.presentation.common.utils.MainItems
import com.google.gson.annotations.SerializedName

data class DashboardModel(
    @SerializedName("ReceivingCount")
    val receivingCount: Int
) {
    fun getCount(item: MainItems): Int? {
        return when(item){
            MainItems.Receiving -> receivingCount
            else -> null
        }
    }
}