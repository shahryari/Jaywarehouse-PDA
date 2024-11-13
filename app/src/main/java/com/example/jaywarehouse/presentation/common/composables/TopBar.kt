package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Border
import com.example.jaywarehouse.ui.theme.Primary

@Composable
fun TopBar(
    title: String,
    onBack: ()-> Unit,
    modifier: Modifier = Modifier,
    subTitle: String = "",
    endIcon: Int? = null,
    onEndClick: ()-> Unit = {}
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier =Modifier
                .shadow(1.mdp,RoundedCornerShape(6.mdp))
                .clip(RoundedCornerShape(6.mdp))
                .background(Color.White)
                .clickable {
                    onBack()
                }
                .padding(12.mdp)
        ) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = "",
                modifier = Modifier.size(24.mdp),
                tint = Black
            )
        }
        Spacer(Modifier.size(5.mdp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subTitle.isNotEmpty())Spacer(Modifier.size(3.mdp))
            if (subTitle.isNotEmpty())MyText(
                text = subTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W500,
                color = Border
            )
        }
        Spacer(Modifier.size(5.mdp))
        if (endIcon!=null){
            Box(
                modifier =Modifier
                    .shadow(1.mdp,RoundedCornerShape(6.mdp))
                    .clip(RoundedCornerShape(6.mdp))
                    .background(Primary)
                    .clickable {
                        onEndClick()
                    }
                    .padding(12.mdp)
            ) {
                Icon(
                    painterResource(endIcon),
                    contentDescription = "",
                    modifier = Modifier.size(24.mdp),
                    tint = Color.White
                )
            }
        } else {

            Spacer(Modifier.size(48.mdp))
        }
    }
}
