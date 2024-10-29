package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import androidx.compose.ui.window.Dialog
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.ErrorRed
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Red

@Composable
fun ErrorDialog(
    onDismiss: ()->Unit,
    message: String
) {
   Dialog(onDismissRequest = onDismiss) {

       Column(
           Modifier
               .fillMaxWidth()
               .clip(RoundedCornerShape(12  .mdp))
               .background(Gray4)
               .padding(vertical = 20.mdp, horizontal = 18.mdp),
       ) {
           MyText(
               text = "Error has been occurred",
               color = ErrorRed,
               style = MaterialTheme.typography.titleMedium,
               fontWeight = FontWeight.SemiBold
           )
           Spacer(modifier = Modifier.size(5.mdp))
           MyText(
               text = message,
               color = Color.Black.copy(0.7f),
               style = MaterialTheme.typography.titleSmall,
               fontWeight = FontWeight.Normal,
           )
           Spacer(modifier = Modifier.size(15.mdp))
           MyText(
               text = "Dismiss",
               color = Black,
               style = MaterialTheme.typography.titleSmall,
               modifier = Modifier.clickable{onDismiss()}.align(Alignment.End),
               fontWeight = FontWeight.SemiBold,
           )
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