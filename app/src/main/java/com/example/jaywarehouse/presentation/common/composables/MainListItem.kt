package com.example.jaywarehouse.presentation.common.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.ui.theme.Primary
import com.example.jaywarehouse.ui.theme.PrimaryContainer
import com.example.jaywarehouse.ui.theme.PrimaryDark

@Composable
fun MainListItem(
    onClick: ()->Unit,
    typeTitle: String? = null,
    modelNumber: String? = null,
    item1 : BaseListItemModel? = null,
    item2 : BaseListItemModel? = null,
    totalTitle: String = "Total",
    totalIcon: Int = R.drawable.vuesax_outline_box_tick,
    total: String = "",
    countTitle: String = "Scan",
    countIcon: Int = R.drawable.scanner,
    count: String = "",
    showFooter: Boolean = true,
) {
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable {
                onClick()
            }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(15.mdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                if(typeTitle!=null)Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = typeTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                } else {
                    Spacer(Modifier.size(10.mdp))
                }
                if (modelNumber!=null) {
                    MyText(
                        text = modelNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                } else {
                    Spacer(Modifier.size(10.mdp))
                }

            }
            if (item1!=null)Spacer(modifier = Modifier.size(10.mdp))
            if (item1!=null)DetailCard(
                item1.title,
                icon = item1.icon,
                detail = item1.value,
                textStyle = item1.style?:MaterialTheme.typography.titleMedium
            )
            if (item2!=null)Spacer(modifier = Modifier.size(8.mdp))
            if (item2!=null)DetailCard(
                item2.title,
                icon = item2.icon,
                detail = item2.value,
                textStyle = item2.style?:MaterialTheme.typography.titleMedium
            )
            if (showFooter)Spacer(modifier = Modifier.size(15.mdp))

        }
        if (showFooter) {
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .background(Primary)
                        .padding(vertical = 7.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(totalIcon),
                        contentDescription = "",
                        modifier = Modifier.size(28.mdp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(7.mdp))
                    MyText(
                        text = "$totalTitle: ${total.toDoubleOrNull()?.removeZeroDecimal()?:total}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    Modifier
                        .weight(1f)
                        .background(PrimaryContainer)
                        .padding(vertical = 7.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(countIcon),
                        contentDescription = "",
                        modifier = Modifier.size(28.mdp),
                        tint = PrimaryDark
                    )
                    Spacer(modifier = Modifier.size(7.mdp))
                    MyText(
                        text = "$countTitle: ${count.toDoubleOrNull()?.removeZeroDecimal()?:count}",
                        color = PrimaryDark,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}