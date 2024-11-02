package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.jaywarehouse.ui.theme.Border
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray4

@Composable
fun ReceivingItem(
    receivingDetailRow: ReceivingDetailRow
) {
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(8.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.mdp))
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
        if (receivingDetailRow.barcode.isNotEmpty() || receivingDetailRow.referenceNumber?.isNotEmpty() == true)Row(
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
            if (receivingDetailRow.barcode.isNotEmpty() || receivingDetailRow.referenceNumber?.isNotEmpty() == true)Spacer(modifier = Modifier.size(5.mdp))
//            DetailCard(
//                title = "Location Code",
//                detail = "40200002.22",
//                icon = R.drawable.vuesax_linear_box,
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(modifier = Modifier.size(5.mdp))
            if (receivingDetailRow.referenceNumber?.isNotEmpty() == true)DetailCard(
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
                .clip(RoundedCornerShape(bottomEnd = 8.mdp, bottomStart = 8.mdp))
                ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomStart = 8.mdp))
                    .background(Gray3)
                    .padding(15.mdp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MyText(
                    "Total: " + receivingDetailRow.quantity.toString(),
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500,
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomEnd = 8.mdp))
                    .background(Gray4)
                    .padding(15.mdp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MyText(
                    "Scan: " + receivingDetailRow.scanCount.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500
                )

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
                .then(
                    if (enableDetail) {
                        Modifier
                            .clickable {
                                showAllDetail = !showAllDetail
                            }
                    } else {
                        Modifier
                    }),
//                .padding(end = 10.mdp, top = 3.mdp, bottom = 3.mdp, start = 3.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = null,
                tint = Black,
                modifier = Modifier.size(24.mdp)
            )
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
    ReceivingItem(ReceivingDetailRow(100,"",200,"3903099390","3333",33,""))

}