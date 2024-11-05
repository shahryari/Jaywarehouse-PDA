package com.example.jaywarehouse.presentation.common.utils

enum class Order(val value: String,val title: String) {
    Asc("asc","Ascending"),Desc("desc","Descending");
    companion object {
        fun getFromValue(value: String): Order? {
            return entries.find { it.value == value }
        }
    }
}