package com.example.jaywarehouse.data.manual_putaway.repository

import com.google.gson.annotations.SerializedName

data class ManualPutawayModel(
    @SerializedName("rows")
    val rows: List<ManualPutawayRow>,
    @SerializedName("total")
    val total: Int
)

class ManualPutawayRow