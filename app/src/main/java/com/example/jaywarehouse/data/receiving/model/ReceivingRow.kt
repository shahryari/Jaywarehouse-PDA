package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReceivingRow(
    @SerializedName("Date")
    val date: String,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("Progress")
    val progress: Int,
    @SerializedName("ReceivingDetailCount")
    val receivingDetailCount: Int,
    @SerializedName("ReceivingDetailScanCount")
    val receivingDetailScanCount: Int,
    @SerializedName("ReceivingDetailSumQuantityScanCount")
    val receivingDetailSumQuantityScanCount: Int,
    @SerializedName("ReceivingID")
    val receivingID: Int,
    @SerializedName("ReceivingNumber")
    val receivingNumber: String,
    @SerializedName("ReceivingTypeTitle")
    val receivingTypeTitle: String,
    @SerializedName("SumQuantity")
    val sumQuantity: Int
) : Serializable