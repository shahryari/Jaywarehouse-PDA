package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Black

@Composable
fun DetailCard(
    title: String,
    detail: String,
    modifier: Modifier = Modifier,
    textStyle : TextStyle = MaterialTheme.typography.titleMedium,
    enableDetail: Boolean = false,
    icon: Int?,
) {
    var showAllDetail by remember { mutableStateOf(false) }
    Column(
        modifier
    ) {
        if (title.isNotEmpty())MyText(
            title,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall
        )
        if (title.isNotEmpty())Spacer(modifier = Modifier.size(7.mdp))
        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .then(
                    if (enableDetail) {
                        Modifier
                            .clickable {
                                showAllDetail = !showAllDetail
                            }
                    } else {
                        Modifier
                    }),
//                .padding(end = 10.mdp, top = 3.mdp, bottom = 3.mdp, start = 3.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null)Icon(
                painterResource(id = icon),
                contentDescription = null,
                tint = Black,
                modifier = Modifier.size(24.mdp)
            )
            if (icon!=null)Spacer(modifier = Modifier.size(7.mdp))
            MyText(
                text = detail,
                style = textStyle,
                fontWeight = FontWeight.Normal,
                maxLines = if (showAllDetail) 3 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
