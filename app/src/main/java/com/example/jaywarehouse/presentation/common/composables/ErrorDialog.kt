package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import androidx.compose.ui.window.Dialog
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Red

@Composable
fun ErrorDialog(
    onDismiss: ()->Unit,
    message: String
) {
   Dialog(onDismissRequest = onDismiss) {
       Box(modifier = Modifier
           .fillMaxSize()
           .padding(bottom = 30.mdp), contentAlignment = Alignment.BottomCenter){
           Column(
               Modifier
                   .fillMaxWidth()
                   .clip(RoundedCornerShape(20.mdp))
                   .background(Black)
                   .padding(vertical = 20.mdp, horizontal = 15.mdp),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               Box(
                   Modifier
                       .clip(CircleShape)
                       .border(5.mdp, Red, CircleShape)
                       .padding(20.mdp)
               ) {
                   Icon(
                       Icons.Default.Clear,
                       contentDescription = "",
                       tint = Red,
                       modifier = Modifier.size(60.mdp)
                   )
               }
               Spacer(modifier = Modifier.size(15.mdp))
               MyText(
                   text = "Error has been occurred",
                   color = Color.White,
                   style = MaterialTheme.typography.titleMedium,
                   fontWeight = FontWeight.SemiBold
               )
               Spacer(modifier = Modifier.size(5.mdp))
               MyText(
                   text = message,
                   color = Color.White.copy(0.6f),
                   style = MaterialTheme.typography.titleSmall,
                   fontWeight = FontWeight.Normal,
                   textAlign = TextAlign.Center
               )
               Spacer(modifier = Modifier.size(15.mdp))
               Button(
                   onClick = onDismiss,
                   contentPadding = PaddingValues(vertical = 15.mdp, horizontal = 40.mdp),
                   colors = ButtonDefaults.buttonColors(containerColor = Red)
               ) {
                   MyText(
                       text = "Dismiss",
                       color = Black,
                       style = MaterialTheme.typography.titleMedium,
                       fontWeight = FontWeight.SemiBold,
                   )
               }
           }
       }
   }
}


@Preview
@Composable
private fun ErrorDialogPreview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        ErrorDialog(
            onDismiss = { /*TODO*/ },
            message = "password or username is wrong please check it again"
        )
    }
}