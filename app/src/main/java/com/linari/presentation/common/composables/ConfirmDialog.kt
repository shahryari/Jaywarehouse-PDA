package com.linari.presentation.common.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.linari.data.common.utils.mdp
import com.linari.ui.theme.Orange


@Composable
fun ConfirmDialog(
    onDismiss: ()->Unit,
    message: AnnotatedString,
    tint: Color = Orange,
    isLoading: Boolean = false,
    onConfirm: ()->Unit
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = "Yes",
        positiveButtonTint = tint,
        negativeButton = "Cancel",
        isLoading = isLoading,
        onPositiveClick = {
            onConfirm()
        },
        onNegativeClick = {
            onDismiss()
        }
    ) {
        MyText(
            text = message,
            fontSize = 17.sp,
            color = Color.Black,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Start
        )
//        if (description.isNotEmpty() && message.isNotEmpty())Spacer(modifier = Modifier.size(5.mdp))
//        if (description.isNotEmpty())MyText(
//            text = description,
//            style = MaterialTheme.typography.bodyLarge,
//            color = DarkGray,
//            fontWeight = FontWeight.W400
//        )
        Spacer(Modifier.size(7.mdp))
    }
}