package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    onDismiss: ()->Unit,
    sortOptions: Map<String,String>,
    selectedSort: String,
    onSelectSort: (String)-> Unit,
    selectedOrder: String,
    onSelectOrder: (String)->Unit
) {
    BasicDialog(
        onDismiss = onDismiss,
        title = "Sort By",
        showCloseIcon = true
    ) {
        Column(Modifier.fillMaxWidth()) {

            for ((i,sort) in sortOptions.entries.withIndex()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectSort(sort.value)
                        }
                        .padding(horizontal = 12.mdp, vertical = 10.mdp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    MyText(
                        text = sort.key,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    MySwitch(
                        active = selectedSort == sort.value,
                        onStateChange = {
                            onSelectSort(sort.value)
                        }
                    )
                }
                Spacer(modifier = Modifier.size(5.mdp))
            }
            Row(Modifier.fillMaxWidth().padding(5.mdp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                for((i,order) in Order.entries.withIndex()) {
                    Row(
                        Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (selectedOrder == order.value) Orange else Color.LightGray)
                            .clickable {
                                onSelectOrder(order.value)
                                onDismiss()
                            }
                            .padding(12.mdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MyText(
                            text = order.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedOrder == order.value) Black else Color.White
                        )
                    }
                    if (i< Order.entries.lastIndex){
                        Spacer(modifier = Modifier.size(12.mdp))
                    }
                }
            }
        }
    }
}