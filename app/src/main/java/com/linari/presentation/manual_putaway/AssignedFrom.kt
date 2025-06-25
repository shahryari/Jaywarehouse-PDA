package com.linari.presentation.manual_putaway

enum class AssignedFrom(val value: String) {
    Form("admin"),Worker("worker");

    companion object {
        fun getFromValue(value: String?) : AssignedFrom? {
            return entries.find { it.value == value?.lowercase()}
        }
    }
}