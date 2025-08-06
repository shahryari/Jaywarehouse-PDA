package com.linari.presentation.common.composables

import android.os.Build
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.linari.BuildConfig
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.presentation.common.utils.DayOfMonth
import com.linari.presentation.common.utils.Month
import com.linari.presentation.common.utils.Year
import com.linari.presentation.common.utils.calculateDayOfMonths
import com.linari.ui.theme.Primary
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun DatePickerDialog(
    onDismiss: ()->Unit,
//    year: Int,
//    month: Int,
//    dayOfMonth: Int,
    selectedDate: String? = null,
    yearsRange: IntRange = 1900..2100,
    onSave: (f1:String,f2: String) -> Unit
) {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = selectedDate?.let { inputFormat.parse(it) } ?: Date()

    val calendar = Calendar.getInstance()
    calendar.time = date
    val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
    val year = calendar.get(Calendar.YEAR)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val days = calculateDayOfMonths(month, year)
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
            years.find { it.value == year }
        )
    }
    var selectedMonth by remember {
        mutableStateOf(
            months.find { it.value == month } ?: Month(
                text = DateFormatSymbols().shortMonths[1],
                value = 1,
                index = 0

            )
        )
    }
    var selectedDayOfMonth by remember {
        mutableStateOf(
            days.find { it.value == dayOfMonth }
        )
    }



    BasicDialog(
        onDismiss,
        positiveButton = stringResource(R.string.save),
        negativeButton = stringResource(R.string.cancel),
        onPositiveClick = {

            onSave(
                "${selectedYear!!.value}-${String.format(Locale.US,"%02d",selectedMonth.value)}-${String.format(Locale.US,"%02d",selectedDayOfMonth!!.value)}",
                "${String.format(Locale.US,"%02d",selectedDayOfMonth!!.value)}/${String.format(Locale.US,"%02d",selectedMonth.value)}/${selectedYear!!.value}"
            )
        }
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(8.mdp))
            , verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

            Picker(
                items = days,
                modifier = Modifier.weight(1f),
                onSelect = { selectedDayOfMonth = it },
                startIndex = days.indexOf(selectedDayOfMonth)
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
                items = years,
                modifier = Modifier.weight(1f),
                onSelect = { selectedYear = it },
                startIndex = years.indexOf(selectedYear)
            )
        }
    }
}


@Preview
@Composable
private fun DatePickerDialogPreview() {
    MyScaffold {
        DatePickerDialog({}) {_,_-> }
    }
}