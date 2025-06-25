package com.example.jaywarehouse.data.auth.models

import com.example.jaywarehouse.presentation.common.utils.Selectable
import com.google.gson.annotations.SerializedName

data class WarehouseModel(
    @SerializedName("Code")
    val code: String,
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Name")
    val name: String
) : Selectable {
    override fun string(): String {
        return name
    }
}


