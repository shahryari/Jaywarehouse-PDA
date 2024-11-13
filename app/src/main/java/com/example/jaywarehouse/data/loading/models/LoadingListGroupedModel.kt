package com.example.jaywarehouse.data.loading.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoadingListGroupedModel(
    @SerializedName("rows")
    val rows: List<LoadingListGroupedRow>,
    @SerializedName("total")
    val total: Int
)

data class LoadingListGroupedRow(
    @SerializedName("CustomerCode")
    val customerCode: String,
    @SerializedName("CustomerName")
    val customerName: String
) : Serializable