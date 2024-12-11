package com.example.jaywarehouse.data.cycle_count.models

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
    val locationCode: String
) : Serializable