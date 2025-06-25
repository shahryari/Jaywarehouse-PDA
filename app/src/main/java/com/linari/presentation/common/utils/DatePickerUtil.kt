package com.linari.presentation.common.utils

import android.icu.util.GregorianCalendar
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate


data class Year(
    val text: String,
    val value: Int,
    val index: Int
) {
    override fun toString(): String {
        return text
    }
}

data class Month(
    val text: String,
    val value: Int,
    val index: Int
) {
    override fun toString(): String {
        return text
    }
}
data class DayOfMonth(
    val text: String,
    val value: Int,
    val index: Int
) {
    override fun toString(): String {
        return text
    }
}




fun calculateDayOfMonths(month: Int, year: Int): List<DayOfMonth> {

    val isLeapYear = GregorianCalendar().isLeapYear(year)

    val month31day = (1..31).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }
    val month30day = (1..30).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }
    val month29day = (1..29).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }
    val month28day = (1..28).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }

    return when(month){
        1 -> { month31day }
        2 -> { if(isLeapYear) month29day else month28day }
        3 -> { month31day }
        4 -> { month30day }
        5 -> { month31day }
        6 -> { month30day }
        7 -> { month31day }
        8 -> { month31day }
        9 -> { month30day }
        10 -> { month31day }
        11 -> { month30day }
        12 -> { month31day }
        else -> { emptyList() }
    }
}