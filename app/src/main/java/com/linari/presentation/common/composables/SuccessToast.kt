package com.linari.presentation.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Green

@Composable
fun SuccessToast(modifier: Modifier = Modifier,message: String) {
    AnimatedVisibility(
        visible = message.isNotEmpty(),
        enter = slideInHorizontally(initialOffsetX = {-it}),
        exit = slideOutHorizontally(targetOffsetX = {-it})
    ) {
        Box(
            modifier.fillMaxWidth()
            .clip(RoundedCornerShape(7.mdp))
            .background(Color.White)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(7.mdp))
                    .background(Green.copy(0.1f))
                    .border(2.mdp, Green, RoundedCornerShape(7.mdp))
                    .padding(7.mdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vuesax_linear_info_circle),
                    contentDescription = "",
                    modifier = Modifier.size(18.mdp),
                    tint = Green
                )
                Spacer(modifier = Modifier.size(7.mdp))
                MyText(
                    text = message,
                    style = MaterialTheme.typography.labelLarge,
                    color = Green,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}