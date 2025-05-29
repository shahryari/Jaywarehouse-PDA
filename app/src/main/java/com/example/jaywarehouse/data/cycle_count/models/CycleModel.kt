package com.example.jaywarehouse.data.cycle_count.models

import com.example.jaywarehouse.presentation.common.composables.Animatable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CycleModel (
    @SerializedName("rows") val rows: List<CycleRow>,
    @SerializedName("total") val total: Int
)



data class CycleRow(
    @SerializedName("Aisle")
    val aisle: String?,
    @SerializedName("Bank")
    val bank: String?,
    @SerializedName("Bay")
    val bay: String?,
    @SerializedName("CycleCountID")
    val cycleCountID: String,
    @SerializedName("CycleCountLocationID")
    val cycleCountLocationID: String,
    @SerializedName("CycleCountWorkerTaskID")
    val cycleCountWorkerTaskID: String,
    @SerializedName("LevelInfo")
    val levelInfo: String?,
    @SerializedName("LocationCode")
    val locationCode: String,
    @SerializedName("IsEmpty")
    val isEmpty: Boolean,
    @SerializedName("DetailCount")
    val detailCount: Int,
    @SerializedName("Counting")
    val counting: Int,
    @SerializedName("TaskCount")
    val taskCount: Int
) : Serializable, Animatable {
    override fun equals(other: Any?): Boolean {
        return other is CycleRow
                && this.cycleCountID == other.cycleCountID
                && this.cycleCountLocationID == other.cycleCountLocationID
                && this.cycleCountWorkerTaskID == other.cycleCountWorkerTaskID
                && this.locationCode == other.locationCode
    }

    override fun key(): String {
        return cycleCountLocationID.toString()
    }
}