package com.linari.presentation.common.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Black

@Composable
fun RefreshIcon(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(3.mdp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotate = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    Box(Modifier.padding(paddingValues)) {
        Icon(
            painter = painterResource(id = R.drawable.refresh),
            contentDescription ="",
            modifier = modifier
                .size(24.mdp)
                .rotate(if (isRefreshing) rotate.value else 0f),
            tint = Black
        )
    }
}