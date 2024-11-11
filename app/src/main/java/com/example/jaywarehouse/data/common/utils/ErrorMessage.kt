package com.example.jaywarehouse.data.common.utils

import com.google.gson.annotations.SerializedName


data class ErrorMessage(
    @SerializedName("Message") val message: String
)

data class ErrorMessages(
    @SerializedName("Messages") val messages: List<String>
)