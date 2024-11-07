package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp

@Composable
fun DetailItem(
    i: Int,
    first: String,
    second: String,
    third: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .padding(10.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MyText(
            text = i.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = Color.Black
        )

        Row {
            Icon(
                painterResource(R.drawable.barcode),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = first,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
        Row {
            Icon(
                painterResource(R.drawable.barcode),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = second,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
        Row {
            Icon(
                painterResource(R.drawable.vuesax_linear_calendar_2),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = third,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
    }
}