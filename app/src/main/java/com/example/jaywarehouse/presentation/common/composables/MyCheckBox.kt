package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.jaywarehouse.data.common.utils.mdp

@Composable
fun MyCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckChange: (checked: Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.mdp))
            .border(1.mdp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(5.mdp))
            .clickable {
                onCheckChange(!checked)
            }
            .padding(3.mdp)
    ){
        if (checked) {
            Icon(
                Icons.Default.Check,
                contentDescription = "",
                Modifier.size(15.mdp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Spacer(modifier = Modifier.size(15.mdp))
        }
    }
}