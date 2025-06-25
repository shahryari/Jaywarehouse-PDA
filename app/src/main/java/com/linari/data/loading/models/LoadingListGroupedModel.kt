package com.linari.data.loading.models

import com.linari.presentation.common.composables.Animatable
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
    val customerCode: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("CustomerTypeTitle")
    val customerTypeTitle: String?
) : Serializable, Animatable {
    override fun key(): String {
        return customerCode?:""
    }

}