package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
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
import coil.size.Size
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Primary

@Composable
fun MyButton(
    modifier : Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Primary,
        contentColor = Color.White
    ),
    shape: Shape = RoundedCornerShape(6.mdp)
) {
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(vertical = 14.mdp),
        modifier = modifier
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.mdp), color = Color.White)
        else MyText(
            text = title,
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.titleMedium,
            color = colors.contentColor
        )
    }
}