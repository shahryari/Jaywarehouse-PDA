package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.jaywarehouse.data.common.utils.mdp

@Composable
fun TitleView(
    modifier: Modifier = Modifier,
    title: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyText(
            title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W500
        )
        Spacer(Modifier.size(3.mdp))
        MyText(
            "*",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Red
        )
    }
}
