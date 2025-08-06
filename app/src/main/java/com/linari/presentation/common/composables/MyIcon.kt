package com.linari.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Black
import com.linari.ui.theme.Border
import com.linari.ui.theme.Gray3

@Composable
fun MyIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    showBorder: Boolean = true,
    background: Color = Color.White,
    tint: Color = Color.Black,
    clickable: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(5.mdp),
    size: Dp = 24.mdp,
    shape: Shape = RoundedCornerShape(4.mdp),
    onClick: (()->Unit)? = null
) {
    Box(modifier = modifier
        .clip(shape)
        .background(background)
        .then(
            if (showBorder) Modifier.border(1.mdp, tint, shape)
            else Modifier
        )
        .then(
            if(onClick != null) Modifier
                .clickable(clickable) {
                    onClick()
                }
            else Modifier
        )
        .padding(paddingValues), contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = "",
            tint = tint,
            modifier = Modifier
                .size(size)

        )
    }
}

@Composable
fun MyIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    showBorder: Boolean = true,
    background: Color = Color.White,
    tint: Color = Color.Black,
    clickable: Boolean = true,
    onClick: (()->Unit)? = null
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(4.mdp))
        .background(background)
        .then(
            if (showBorder) Modifier.border(1.mdp, tint, RoundedCornerShape(4.mdp))
            else Modifier
        )
        .then(
            if(onClick != null) Modifier
                .clickable(clickable) {
                    onClick()
                }
            else Modifier
        )
        .padding(5.mdp), contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = tint,
            modifier = Modifier
                .size(24.mdp)

        )
    }
}