package com.example.jaywarehouse.presentation.counting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.jaywarehouse.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.ReceivingItem
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination


@Destination(style = ScreenTransition::class)
@Composable
fun CountingInceptionScreen() {

    CountingInceptionContent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountingInceptionContent(
    state: CountingInceptionContract.State = CountingInceptionContract.State(),
    onEvent: (CountingInceptionContract.Event)->Unit = {}
) {
    MyScaffold {
        Column(
            Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
            TopBar(
                title = state.countingDetailRow?.referenceNumber?:"",
                subTitle = "Counting/Inception",
                onBack = {},
                endIcon = R.drawable.tick
            )
            Spacer(Modifier.size(20.mdp))
            LazyColumn {
                stickyHeader{
                    Column {
                        if (state.countingDetailRow!=null){
                            ReceivingItem(state.countingDetailRow)
                        }
                        Spacer(Modifier.size(10.mdp))
                        Row(Modifier.fillMaxWidth()) {
                            InputTextField(
                                state.quantity,
                                onValueChange = {},
                                modifier = Modifier.weight(1f),
                                leadingIcon = R.drawable.box_search,
                                label = "Quantity",
                            )
                            Spacer(Modifier.size(5.mdp))
                            InputTextField(
                                state.quantityInPacket,
                                onValueChange = {},
                                modifier = Modifier.weight(1f),
                                leadingIcon = R.drawable.barcode,
                                label = "Quantity In Packet",
                            )
                        }
                        Spacer(Modifier.size(10.mdp))
                        InputTextField(
                            state.batchNumber,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = R.drawable.keyboard,
                            label = "Batch Number",
                        )
                        Spacer(Modifier.size(10.mdp))
                        InputTextField(
                            state.expireDate,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = R.drawable.calendar_add,
                            label = "Expire Date",
                        )
                        Spacer(Modifier.size(10.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.mdp))
                                .background(Primary.copy(0.2f))
                                .border(1.mdp, Primary, RoundedCornerShape(6.mdp))
                                .padding(9.mdp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            MyText(
                                text = state.count.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary,
                                fontWeight = FontWeight.W500,
                            )
                            MyIcon(
                                icon = Icons.Default.Add, showBorder = false,
                                background = Color.Transparent,
                                tint = Primary,
                            ) { }
                        }
                        Spacer(Modifier.size(20.mdp))
                    }
                }
                items(3){
                    CountingInceptionDetailItem()
                    Spacer(Modifier.size(5.mdp))
                }
            }
        }
    }




    
}

@Composable
fun CountingInceptionDetailItem(
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .padding(10.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MyText(
            text = "1",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = Color.Black
        )

        Row {
            Icon(
                painterResource(R.drawable.barcode),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = "123456",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
        Row {
            Icon(
                painterResource(R.drawable.barcode),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = "123456",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
        Row {
            Icon(
                painterResource(R.drawable.vuesax_linear_calendar_2),
                contentDescription = "",
                modifier = Modifier.size(20.mdp),
                tint = Color.Black
            )
            Spacer(Modifier.size(10.mdp))
            MyText(
                text = "today",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        }
    }
}



@Preview
@Composable
private fun CountingInceptionPreview() {
    CountingInceptionContent()
}