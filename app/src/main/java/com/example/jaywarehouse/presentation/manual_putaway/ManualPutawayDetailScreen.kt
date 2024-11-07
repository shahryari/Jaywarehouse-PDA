package com.example.jaywarehouse.presentation.manual_putaway

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayDetailRow
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.DetailItem
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.counting.CountingInceptionDetailItem
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayDetailContract
import com.example.jaywarehouse.ui.theme.Primary

@Composable
fun ManualPutawayDetailScreen() {

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ManualPutawayDetailContent(
    state: ManualPutawayDetailContract.State = ManualPutawayDetailContract.State(),
    onEvent: (ManualPutawayDetailContract.Event) -> Unit
) {
    MyScaffold(
        error = state.error,
        onCloseError = {
            onEvent(ManualPutawayDetailContract.Event.OnCloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(ManualPutawayDetailContract.Event.HideToast)
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
            TopBar(
                title = "Manual Putaway",
                onBack = {
                    onEvent(ManualPutawayDetailContract.Event.OnNavBack)
                },
                endIcon = R.drawable.tick,
                onEndClick = {
                    onEvent(ManualPutawayDetailContract.Event.OnSubmit)
                }
            )
            Spacer(Modifier.size(20.mdp))
            LazyColumn {
                stickyHeader{
                    Column {
                        if (state.putaway!=null){
                            ManualPutawayItem(state.putaway)
                        }
                        Spacer(Modifier.size(10.mdp))
                        Row(Modifier.fillMaxWidth()) {
                            InputTextField(
                                state.quantity,
                                onValueChange = {
                                    onEvent(ManualPutawayDetailContract.Event.OnQuantityChange(it))
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = R.drawable.box_search,
                                label = "Quantity",
                            )
                            Spacer(Modifier.size(5.mdp))
                            InputTextField(
                                state.quantityInPacket,
                                onValueChange = {
                                    onEvent(ManualPutawayDetailContract.Event.OnQuantityInPacketChange(it))
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = R.drawable.barcode,
                                label = "Quantity In Packet",
                            )
                        }
                        Spacer(Modifier.size(10.mdp))
                        InputTextField(
                            state.locationCode,
                            onValueChange = {
                                onEvent(ManualPutawayDetailContract.Event.OnLocationCodeChange(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = R.drawable.keyboard,
                            label = "Batch Number",
                        )
                        Spacer(Modifier.size(10.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.mdp))
                                .background(Primary.copy(0.2f))
                                .clickable {
                                    onEvent(ManualPutawayDetailContract.Event.OnAddClick)
                                }
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
                itemsIndexed(state.details.reversed()){i,it->
                    ManualPutawayDetailItem(state.details.size-i,it)
                    Spacer(Modifier.size(5.mdp))
                }
            }
        }
    }
}


@Composable
fun ManualPutawayDetailItem(
    i: Int,
    detail: ManualPutawayDetailRow
) {
    DetailItem(
        i,
        "",
        "",
        "today"
    )
}

@Preview
@Composable
private fun ManualPutawayDetailPreview() {
    ManualPutawayDetailContent(
        onEvent = {}
    )
}


