package com.linari.presentation.common.utils

import com.google.gson.annotations.SerializedName

data class SortItem(
    val title: String,
    val sort: String,
    val order: Order
)

data class SortItemDto(
    @SerializedName("index")
    val index: Int,
    @SerializedName("sort")
    val sort: String,
    @SerializedName("order")
    val order: String
)