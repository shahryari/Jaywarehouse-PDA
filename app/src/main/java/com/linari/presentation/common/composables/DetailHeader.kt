package com.linari.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Primary

@Composable
fun DetailHeader(
    firstTitle: String,
    secondTitle: String,
    thirdTitle: String,
    forthTitle: String,
    fifthTitle: String
) {
    Column(
        modifier = Modifier
            .shadow(1.mdp,RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .background(Color.Black.copy(0.2f))
            .padding(10.mdp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = "#",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
            Spacer(Modifier.size(10.mdp))

            Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                MyText(
                    text = firstTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }
            Spacer(Modifier.size(5.mdp))
            Row(modifier = Modifier.weight(0.5f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                MyText(
                    text = secondTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }
            Spacer(Modifier.size(5.mdp))
            Row(modifier = Modifier.weight(0.5f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                MyText(
                    text = thirdTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }

            Spacer(Modifier.size(5.mdp))
            Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                MyText(
                    text = forthTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }
            Spacer(Modifier.size(5.mdp))
            Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                MyText(
                    text = fifthTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.Black
                )
            }
        }
    }
}