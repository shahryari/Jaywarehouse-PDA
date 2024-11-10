package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import androidx.compose.ui.window.Dialog
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Primary

@Composable
fun BasicDialog(
    onDismiss:()->Unit,
    positiveButton: String? = null,
    positiveButtonTint: Color = Primary,
    onPositiveClick:()->Unit = {},
    negativeButton: String? = null,
    onNegativeClick: (()-> Unit)? = null,
    showCloseIcon: Boolean = false,
    title: String = "",
    isLoading: Boolean = false,
    content: @Composable ColumnScope.()->Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.mdp))
                .background(Gray4)
                .padding(15.mdp),
        ) {
            Spacer(Modifier.size(7.mdp))
            content()
            Spacer(modifier = Modifier.size(12.mdp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (negativeButton!=null)Button(
                    onClick = onNegativeClick ?: onDismiss,
                    shape = RoundedCornerShape(4.mdp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gray4
                    ),
                    contentPadding = PaddingValues(vertical = 12.mdp, horizontal = 20.mdp),
                ) {
                    MyText(
                        text = negativeButton,
                        color = Color(0xFF0F0F0F),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (negativeButton!=null && positiveButton != null){
                    Spacer(modifier = Modifier.size(10.mdp))
                }
                if (positiveButton!=null)Button(
                    onClick = onPositiveClick,
                    shape = RoundedCornerShape(4.mdp),
                    contentPadding = PaddingValues(vertical =  12.mdp, horizontal = 20.mdp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = positiveButtonTint.copy(0.1f)
                    ),
                    enabled = !isLoading,
                ) {
                    Box {
                        MyText(
                            text = positiveButton,
                            color = if(isLoading) Color.Transparent else positiveButtonTint,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.W500
                        )
                        if (isLoading) CircularProgressIndicator(
                            modifier = Modifier.size(21.mdp),
                            color = positiveButtonTint
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BasicDialogPreview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {

        BasicDialog(
            onDismiss = {},
            positiveButton = "Yes",
            negativeButton = "Cancel"
        ){}
    }
}