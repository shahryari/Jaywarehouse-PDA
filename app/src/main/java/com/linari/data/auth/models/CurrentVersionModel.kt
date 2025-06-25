package com.linari.data.auth.models


import com.google.gson.annotations.SerializedName

data class CurrentVersionModel(
    @SerializedName("CurrentVersion")
    val currentVersion: Int,
    @SerializedName("DownloadUrl")
    val downloadUrl: String,
    @SerializedName("ShowVersion")
    val showVersion: String
)