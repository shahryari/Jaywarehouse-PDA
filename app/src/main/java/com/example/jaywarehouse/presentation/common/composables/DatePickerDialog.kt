package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.common.utils.DayOfMonth
import com.example.jaywarehouse.presentation.common.utils.Month
import com.example.jaywarehouse.presentation.common.utils.Year
import com.example.jaywarehouse.presentation.common.utils.calculateDayOfMonths
import com.example.jaywarehouse.ui.theme.Primary
import java.text.DateFormatSymbols
import java.time.LocalDate


@Composable
fun DatePickerDialog(
    onDismiss: ()->Unit,
//    year: Int,
//    month: Int,
//    dayOfMonth: Int,
    yearsRange: IntRange = 1900..2100,
    onSave: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    var selectedYear by remember {
        mutableIntStateOf(LocalDate.now().year)
    }
    var selectedMonth by remember {
        mutableIntStateOf(LocalDate.now().monthValue)
    }
    var selectedDayOfMonth by remember {
        mutableIntStateOf(LocalDate.now().dayOfMonth)
    }
    val days = calculateDayOfMonths(selectedMonth, selectedYear)

    val months = (1..12).map {
        Month(
            text =
//            if(size.width / 3 < 55.dp){
                DateFormatSymbols().shortMonths[it - 1]
//            } else
//                DateFormatSymbols().months[it - 1]
            ,
            value = it,
            index = it - 1
        )
    }

    val years = yearsRange.map {
        Year(
            text = it.toString(),
            value = it,
            index = yearsRange.indexOf(it)
        )
    }
    BasicDialog(
        onDismiss,
        positiveButton = "Save",
        negativeButton = "Cancel",
        onPositiveClick = { onSave(selectedYear, selectedMonth, selectedDayOfMonth) }
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(8.mdp))
            , verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Picker(
                items = years,
                modifier = Modifier.weight(1f),
                onSelect = { selectedYear = it.value },
                startIndex = years.indexOf(Year(text = selectedYear.toString(), value = selectedYear, index = yearsRange.indexOf(selectedYear)))
            )
            Spacer(Modifier.size(10.mdp))
            Picker(
                items = months,
                modifier = Modifier.weight(1f),
                onSelect = { selectedMonth = it.value },
                startIndex = months.indexOf(Month(text = DateFormatSymbols().months[selectedMonth - 1], value = selectedMonth, index = selectedMonth - 1))
            )
            Spacer(Modifier.size(10.mdp))
            Picker(
                items = days,
                modifier = Modifier.weight(1f),
                onSelect = { selectedDayOfMonth = it.value },
                startIndex = days.indexOf(DayOfMonth(text = selectedDayOfMonth.toString(), value = selectedDayOfMonth, index = selectedDayOfMonth - 1))
            )
        }
    }
}


@Preview
@Composable
private fun DatePickerDialogPreview() {
    MyScaffold {
        DatePickerDialog({}) {_,_,_-> }
    }
}