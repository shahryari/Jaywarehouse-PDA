package com.linari.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Black
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Primary

@Composable
fun RowCountView(
    modifier: Modifier = Modifier,
    current: Int,
    group: Int,
    total: Int
) {
    Row(
        modifier
            .shadow(1.mdp)
            .fillMaxWidth()
            .background(
                Gray3
            )
            .padding(12.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        MyText(
            text = "${current.coerceAtMost(total)} ",
            color = Primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        MyText(
            text = " ${stringResource(R.string.of)} ",
            color = Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        MyText(
            text = "$group",
            color = Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        MyText(
            text = " ${stringResource(R.string.from)} ",
            color = Black,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        MyText(
            text = "$total",
            color = Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}