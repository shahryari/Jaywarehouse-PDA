package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3

@Composable
fun MyIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    onClick: ()->Unit
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(7.mdp))
        .background(Gray3)
        .clickable {
            onClick()
        }
        .padding(3.mdp), contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = Black,
            modifier = Modifier
                .size(24.mdp)

        )
    }
}