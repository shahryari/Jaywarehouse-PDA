package com.linari.data.auth.models

import com.linari.presentation.common.utils.Selectable
import com.google.gson.annotations.SerializedName

data class WarehouseModel(
    @SerializedName("Code")
    val code: String,
    @SerializedName("Id")
    val id: Long,
    @SerializedName("Name")
    val name: String,
    @SerializedName("HasBoxOnShipping")
    val hasBoxOnShipping: Boolean,
    @SerializedName("LocationBase")
    val locationBase: Boolean,
    @SerializedName("OnPickCacncelLocationCode")
    val onPickCancelLocationCode: String?,
    @SerializedName("EnableTransferOnPickCancel")
    val enableTransferOnPickCancel: Boolean
) : Selectable {
    override fun string(): String {
        return name
    }
}


