package com.example.jaywarehouse.data.receiving.model

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName

data class ReceivingDetailCountModel(
    @SerializedName("BatchNumber")
    val batchNumber: String?,
    @SerializedName("CountQuantity")
    val countQuantity: Double,
    @SerializedName("ExpDate")
    val expireDate: String?,
    @SerializedName("PCB")
    val pCB: Double,
    @SerializedName("Pack")
    val pack: Int?,
    @SerializedName("Piece")
    val piece: Int?,
    @SerializedName("ReceivingWorkerTaskCountID")
    val receivingWorkerTaskCountID: Int,
    @SerializedName("ReceivingWorkerTaskID")
    val receivingWorkerTaskID: Int
): Animatable {
    override fun key(): String {
        return receivingWorkerTaskCountID.toString()
    }

}

data class ReceivingDetailGetItemsModel(
    @SerializedName("rows") val rows: List<ReceivingDetailCountModel>,
    @SerializedName("total") val total: Int,
    @SerializedName("Task") val receivingDetailRow: ReceivingDetailRow,
    @SerializedName("PCB") val pcb: PcbModel
)
data class PcbModel(
    @SerializedName("DefaultPcb")
    val defaultPcb: Double?,
    @SerializedName("IsWeight")
    val isWeight: Boolean,
    @SerializedName("Pcb")
    val pcb: Double?
)