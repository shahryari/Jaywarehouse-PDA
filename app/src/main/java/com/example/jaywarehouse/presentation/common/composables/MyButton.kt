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
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.ui.theme.Primary

@Composable
fun MyButton(
    modifier : Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Primary,
        contentColor = Color.White,
        disabledContainerColor = Primary.copy(0.5f)
    ),
    shape: Shape = RoundedCornerShape(6.mdp)
) {
    val color = if(enabled && !isLoading) colors.contentColor else colors.contentColor
    Button(
        onClick = onClick,
        shape = shape,
        colors = colors,
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(vertical = 14.mdp),
        modifier = modifier
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.mdp), color = color)
        else MyText(
            text = title,
            fontWeight = FontWeight.W500,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}