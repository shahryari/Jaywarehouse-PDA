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
import com.example.jaywarehouse.ui.theme.Orange

@Composable
fun BasicDialog(
    onDismiss:()->Unit,
    positiveButton: String? = null,
    onPositiveClick:()->Unit = {},
    negativeButton: String? = null,
    onNegativeClick: ()-> Unit = {},
    showCloseIcon: Boolean = false,
    title: String = "",
    isLoading: Boolean = false,
    content: @Composable ColumnScope.()->Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.mdp), contentAlignment = Alignment.BottomCenter){
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.mdp))
                    .background(Black)
                    .padding(12.mdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {

                    if(showCloseIcon)Box(
                        Modifier
                            .clip(CircleShape)
                            .border(1.mdp, Color.White, CircleShape)
                            .clickable { onDismiss() }
                            .padding(3.mdp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription ="",
                            modifier = Modifier.size(18.mdp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    MyText(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.size(10.mdp))
                content()
                Spacer(modifier = Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    if (negativeButton!=null)Button(
                        onClick = onNegativeClick,
                        shape = RoundedCornerShape(20.mdp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray
                        ),
                        contentPadding = PaddingValues(15.mdp),
                        modifier = Modifier.weight(1f)
                    ) {
                        MyText(
                            text = negativeButton,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (negativeButton!=null && positiveButton != null){
                        Spacer(modifier = Modifier.size(5.mdp))
                    }
                    if (positiveButton!=null)Button(
                        onClick = onPositiveClick,
                        shape = RoundedCornerShape(20.mdp),
                        contentPadding = PaddingValues(vertical = 15.mdp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange
                        ),
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(21.mdp),color = Color.White)
                        else MyText(
                            text = positiveButton,
                            color = Black,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
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
            positiveButton = "ok",
            isLoading = true,
            negativeButton = "cancel"
        ){}
    }
}