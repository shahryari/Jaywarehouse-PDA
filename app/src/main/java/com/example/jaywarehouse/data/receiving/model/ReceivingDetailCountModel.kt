package com.example.jaywarehouse.data.receiving.model

import com.google.gson.annotations.SerializedName

data class ReceivingDetailCountModel(
    @SerializedName("CountQuantity")
    val quantity: Int,
    @SerializedName("EntityState")
    val entityState: String,
    @SerializedName("ReceivingWorkerTaskCountID")
    val receivingWorkerTaskCountId: Int?,
    @SerializedName("ReceivingWorkerTaskID")
    val receivingWorkerTaskId: Int?,
    @SerializedName("BatchNumber")
    val batchNumber: String,
    @SerializedName("ExpireDateString")
    val expireDate: String
)