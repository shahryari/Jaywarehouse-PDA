package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Green
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Red

@Composable
fun ReceivingItem(
    receivingDetailRow: ReceivingDetailRow,
    onRemove: ()->Unit
) {
    Column(
        Modifier
            .shadow(3.mdp, RoundedCornerShape(10.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .background(Color.White)
    ) {
        if(receivingDetailRow.model.isNotEmpty()) Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 15.mdp, end = 15.mdp, top = 15.mdp)
        ) {
            DetailCard(
                title = "Model",
                detail = receivingDetailRow.model,
                icon = R.drawable.vuesax_outline_3d_cube_scan,
                enableDetail = true,
                modifier = Modifier.weight(1f)
            )

        }
        if (receivingDetailRow.barcode.isNotEmpty() || receivingDetailRow.referenceNumber.isNotEmpty())Row(
            Modifier
                .fillMaxWidth()
                .padding(15.mdp)
        ) {
            if (receivingDetailRow.barcode.isNotEmpty())DetailCard(
                title = "Item Code",
                detail = receivingDetailRow.barcode,
                icon = R.drawable.barcode,
                modifier = Modifier.weight(1f),
                enableDetail = true
            )
            if (receivingDetailRow.barcode.isNotEmpty() || receivingDetailRow.referenceNumber.isNotEmpty())Spacer(modifier = Modifier.size(5.mdp))
//            DetailCard(
//                title = "Location Code",
//                detail = "40200002.22",
//                icon = R.drawable.vuesax_linear_box,
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(modifier = Modifier.size(5.mdp))
            if (receivingDetailRow.referenceNumber.isNotEmpty())DetailCard(
                title = "Ref Number",
                detail = receivingDetailRow.referenceNumber,
                icon = R.drawable.note,
                enableDetail = true,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 10.mdp, bottomStart = 10.mdp))
                .background(Gray3)
                .padding(15.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {

                MyText(
                    "Total: "+receivingDetailRow.quantity.toString(),
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            val percentage = receivingDetailRow.scanCount.toFloat()/receivingDetailRow.quantity.toFloat()
            val color = when(percentage){
                in 0f..0.99f-> Orange
                1f -> Green
                else -> Red
            }
            Row(
                Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MyText(
                    "Scan: "+receivingDetailRow.scanCount.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(7.mdp))
                    .background(Red.copy(0.2f))
                    .clickable {
                        onRemove()
                    }
                    .padding(vertical = 12.mdp, horizontal = 20.mdp)){
                    Spacer(modifier = Modifier
                        .height(3.mdp)
                        .width(15.mdp)
                        .background(Red))
                }
                Spacer(modifier = Modifier.size(10.mdp))
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    detail: String,
    modifier: Modifier = Modifier,
    textStyle : TextStyle = MaterialTheme.typography.titleMedium,
    enableDetail: Boolean = false,
    icon: Int,
) {
    var showAllDetail by remember { mutableStateOf(false) }
    Column(
        modifier
    ) {
        MyText(
            title,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.size(7.mdp))
        Row(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clip(CircleShape)
                .background(Gray2)
                .then(
                   if (enableDetail) {
                       Modifier
                           .clickable {
                               showAllDetail = !showAllDetail
                           }
                   } else {
                       Modifier
                   })
                .padding(end = 10.mdp, top = 3.mdp, bottom = 3.mdp, start = 3.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .background(Gray3)
                .padding(5.mdp)
            ){
                Icon(
                    painterResource(id = icon),
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.mdp)
                )
            }
            Spacer(modifier = Modifier.size(7.mdp))
            MyText(
                text = detail,
                style = textStyle,
                fontWeight = FontWeight.Normal,
                maxLines = if (showAllDetail) 3 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun ReceivingItemPreview() {
    ReceivingItem(ReceivingDetailRow(100,"",200,"3903099390","3333",33,"")){}

}