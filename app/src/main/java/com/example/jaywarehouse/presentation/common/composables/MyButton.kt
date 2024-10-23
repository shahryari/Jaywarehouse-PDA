package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Black

@Composable
fun MyButton(
    modifier : Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Black,
        contentColor = Color.White
    ),
    shape: Shape = RoundedCornerShape(10.mdp)
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        modifier = modifier
    ) {
        if (isLoading) CircularProgressIndicator()
        else MyText(
            text = title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium,
            color = colors.contentColor
        )
    }
}