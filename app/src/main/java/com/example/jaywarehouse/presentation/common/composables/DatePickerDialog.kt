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
import androidx.compose.runtime.mutableStateOf
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

    val days = calculateDayOfMonths(LocalDate.now().monthValue, LocalDate.now().year)
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
    var selectedYear by remember {
        mutableStateOf(
            years.find { it.value == LocalDate.now().year }
        )
    }
    var selectedMonth by remember {
        mutableStateOf(
            months.find { it.value == LocalDate.now().monthValue } ?: Month(
                text = DateFormatSymbols().shortMonths[1],
                value = 1,
                index = 0

            )
        )
    }
    var selectedDayOfMonth by remember {
        mutableStateOf(
            days.find { it.value == LocalDate.now().dayOfMonth }
        )
    }



    BasicDialog(
        onDismiss,
        positiveButton = "Save",
        negativeButton = "Cancel",
        onPositiveClick = { onSave(selectedYear!!.value, selectedMonth.value, selectedDayOfMonth!!.value) }
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(8.mdp))
            , verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Picker(
                items = years,
                modifier = Modifier.weight(1f),
                onSelect = { selectedYear = it },
                startIndex = years.indexOf(selectedYear)
            )

            Spacer(Modifier.size(10.mdp))
            Picker(
                items = months,
                modifier = Modifier.weight(1f),
                onSelect = { selectedMonth = it },
                startIndex = months.indexOf(selectedMonth)
            )
            Spacer(Modifier.size(10.mdp))
            Picker(
                items = days,
                modifier = Modifier.weight(1f),
                onSelect = { selectedDayOfMonth = it },
                startIndex = days.indexOf(selectedDayOfMonth)
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