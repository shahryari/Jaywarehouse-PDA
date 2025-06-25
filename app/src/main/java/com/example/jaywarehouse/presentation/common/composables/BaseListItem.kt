package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.Red

data class BaseListItemModel(
    val title: String,
    val value: String,
    val icon: Int,
    val style: TextStyle? = null
)

@Composable
fun BaseListItem(
    modifier: Modifier = Modifier,
    onClick:()->Unit,
    item1: BaseListItemModel? = null,
    item2: BaseListItemModel? = null,
    item3: BaseListItemModel? = null,
    item4: BaseListItemModel? = null,
    item5: BaseListItemModel? = null,
    item6: BaseListItemModel? = null,
    item7: BaseListItemModel? = null,
    item8: BaseListItemModel? = null,
    item9: BaseListItemModel? = null,
    showDeleteButton: Boolean = false,
    onRemove: ()->Unit = {},
    quantity: Double?,
    quantityTitle: String = "Total",
    quantityIcon: Int? = null,
    scan: Double?,
    enableShowDetail: Boolean = false,
    scanTitle: String = "Scan",
    showFooter: Boolean = true,
    expandable: Boolean = false,
    scanContent: (@Composable ()->Unit)? = null,
    scanIcon: Int? = null
) = BaseListItem(
    modifier = modifier,
    onClick = onClick,
    item1 = item1,
    item2 = item2,
    item3 = item3,
    item4 = item4,
    item5 = item5,
    item6 = item6,
    item7 = item7,
    item8 = item8,
    item9 = item9,
    showDeleteButton = showDeleteButton,
    onRemove = onRemove,
    quantity = quantity?.removeZeroDecimal()?.toString(),
    enableShowDetail = enableShowDetail,
    quantityTitle = quantityTitle,
    quantityIcon = quantityIcon,
    scan = scan?.removeZeroDecimal()?.toString(),
    scanTitle = scanTitle,
    showFooter = showFooter,
    expandable = expandable,
    scanContent = scanContent,
    scanIcon = scanIcon
)

@Composable
fun BaseListItem(
    modifier: Modifier = Modifier,
    onClick:()->Unit,
    item1: BaseListItemModel? = null,
    item2: BaseListItemModel? = null,
    item3: BaseListItemModel? = null,
    item4: BaseListItemModel? = null,
    item5: BaseListItemModel? = null,
    item6: BaseListItemModel? = null,
    item7: BaseListItemModel? = null,
    item8: BaseListItemModel? = null,
    item9: BaseListItemModel? = null,
    showDeleteButton: Boolean = false,
    onRemove: ()->Unit = {},
    quantity: String?,
    primary: Boolean = false,
    quantityTitle: String = "Total",
    quantityIcon: Int? = null,
    scan: String?,
    enableShowDetail: Boolean = false,
    expandable: Boolean = false,
    scanTitle: String = "Scan",
    showFooter: Boolean = true,
    scanContent: (@Composable ()->Unit)? = null,
    scanIcon: Int? = null
) {

    val showItem1 = item1 != null && item1.value.isNotEmpty()
    val showItem2 = item2 != null && item2.value.isNotEmpty()
    val showItem3 = item3 != null && item3.value.isNotEmpty()
    val showItem4 = item4 != null && item4.value.isNotEmpty()
    val showItem5 = item5 != null && item5.value.isNotEmpty()
    val showItem6 = item6 != null && item6.value.isNotEmpty()
    val showItem7 = item7 != null && item7.value.isNotEmpty()
    val showItem8 = item8 != null && item8.value.isNotEmpty()
    val showItem9 = item9 != null && item9.value.isNotEmpty()
    var expended by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .shadow(3.mdp, RoundedCornerShape(10.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .clickable {
                onClick()
            }
            .focusable(false)
            .background(Color.White)
            .background(if (primary) Primary.copy(0.2f) else  Color.White)
    ) {
        AnimatedVisibility(visible = showItem1) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.mdp, start = 12.mdp, end = 12.mdp)
            ) {
                if (item1!=null)DetailCard(
                    title = item1.title,
                    detail = item1.value,
                    icon = item1.icon,
                    enableDetail = enableShowDetail,
                    modifier = Modifier.weight(1f),
                    textStyle = item1.style?:MaterialTheme.typography.titleMedium.copy(color = Color.Black)
                )
            }
        }
        AnimatedVisibility(visible = showItem2 || showItem3) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.mdp, start = 12.mdp, end = 12.mdp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showItem2)DetailCard(
                    title = item2!!.title,
                    detail = item2.value,
                    icon = item2.icon,
                    modifier = Modifier.weight(1f),
                    enableDetail = enableShowDetail,
                    textStyle = item2.style?:MaterialTheme.typography.titleMedium.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.size(5.mdp))
                if(showItem3)DetailCard(
                    title = item3!!.title,
                    detail = item3.value,
                    icon =item3.icon,
                    modifier = Modifier.weight(1f),
                    enableDetail = enableShowDetail,
                    textStyle = item3.style?:MaterialTheme.typography.titleMedium.copy(color = Color.Black)
                )
                if(showDeleteButton)Spacer(modifier = Modifier.size(5.mdp))
                if (showDeleteButton){
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.mdp))
                            .background(Red.copy(0.1f))
                            .clickable {
                                onRemove()
                            }
                            .padding(7.mdp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.vuesax_linear_card_remove),
                            contentDescription = "",
                            modifier = Modifier.size(25.mdp),
                            tint = Red
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = showItem4 || showItem5) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.mdp, start = 12.mdp, end = 12.mdp)
            ) {
                if (showItem4)DetailCard(
                    title = item4!!.title,
                    detail = item4.value,
                    icon = item4.icon,
                    modifier = Modifier.weight(1f),
                    enableDetail = enableShowDetail,
                    textStyle = item4.style?:MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.size(5.mdp))
                if(showItem5)DetailCard(
                    title = item5!!.title,
                    detail = item5.value,
                    icon =item5.icon,
                    enableDetail = enableShowDetail,
                    modifier = Modifier.weight(1f),
                    textStyle = item5.style?:MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
        }
        AnimatedVisibility(visible = (showItem6 || showItem7) && (expended || !expandable)) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.mdp, start = 12.mdp, end = 12.mdp)
            ) {
                if (showItem6) DetailCard(
                    title = item6!!.title,
                    detail = item6.value,
                    icon = item6.icon,
                    modifier = Modifier.weight(1f),
                    enableDetail = enableShowDetail,
                    textStyle = item6.style ?: MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.size(5.mdp))
                if (showItem7) DetailCard(
                    title = item7!!.title,
                    detail = item7.value,
                    icon = item7.icon,
                    enableDetail = enableShowDetail,
                    modifier = Modifier.weight(1f),
                    textStyle = item7.style ?: MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
            }
        }
        AnimatedVisibility(visible = (showItem8 || showItem9) && (expended || !expandable)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.mdp, start = 12.mdp, end = 12.mdp)
            ) {
                if (showItem8) DetailCard(
                    title = item8!!.title,
                    detail = item8.value,
                    icon = item8.icon,
                    modifier = Modifier.weight(1f),
                    enableDetail = enableShowDetail,
                    textStyle = item8.style ?: MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.size(5.mdp))
                if (showItem9) DetailCard(
                    title = item9!!.title,
                    detail = item9.value,
                    icon = item9.icon,
                    enableDetail = enableShowDetail,
                    modifier = Modifier.weight(1f),
                    textStyle = item9.style ?: MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    )
                )
            }
        }
        Spacer(Modifier.size(8.mdp))
        if (expandable && !expended)IconButton(
            onClick = {
                expended = !expended
            },
            modifier = Modifier.align(Alignment.Start),
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "",
                modifier = Modifier.rotate(90f),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(8.mdp))
        if (showFooter){
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                if (quantity != null)Row(
                    Modifier
                        .weight(1f)
                        .background(
                            if (primary) Primary else Gray3
                        )
                        .padding(12.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (quantityIcon!=null){
                        Icon(
                            painter = painterResource(id = quantityIcon),
                            contentDescription = "",
                            modifier = Modifier.size(28.mdp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.size(7.mdp))
                    }
                    MyText(
                        text = "$quantityTitle${if (quantityTitle.isNotEmpty()) ": " else ""}$quantity",
                        color = if (primary) Color.White else Black,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (scan!=null)Row(
                    Modifier
                        .weight(1f)
                        .background(
                            if (primary) Primary.copy(0.2f) else Gray4
                        )
                        .padding(12.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (scanIcon!=null){
                            Icon(
                                painter = painterResource(id = scanIcon),
                                contentDescription = "",
                                modifier = Modifier.size(28.mdp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        MyText(
                            text = "$scanTitle${if (scanTitle.isNotEmpty()) ": " else ""}$scan",
                            color = if (primary) Primary else Black,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (scanContent!=null){
                        scanContent()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BaseListItemPreview() {
    BaseListItem(
        onClick = { /*TODO*/ },
        item1 = BaseListItemModel("Model", "Model", R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Item Code", "Item Code", R.drawable.fluent_barcode_scanner_20_regular),
        item3 = BaseListItemModel("Location Code", "Location Code test test test test", R.drawable.location),
        showFooter = false,
        quantity = 10.0, scan = 10.0
    )
}