package com.example.jaywarehouse.presentation.counting

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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.DetailItem
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingInceptionViewModel
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun CountingInceptionScreen(
    navigator: DestinationsNavigator,
    detail: ReceivingDetailRow,
    receivingId: Int,
    viewModel: CountingInceptionViewModel = koinViewModel(
        parameters = {
            parametersOf(
                detail,
                receivingId
            )
        }
    )
) {

    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                CountingInceptionContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }

    CountingInceptionContent(state = state, onEvent = onEvent)

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountingInceptionContent(
    state: CountingInceptionContract.State = CountingInceptionContract.State(),
    onEvent: (CountingInceptionContract.Event)->Unit = {}
) {
    MyScaffold(
        error = state.error,
        onCloseError = {
            onEvent(CountingInceptionContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(CountingInceptionContract.Event.HideToast)
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
            TopBar(
                title = state.countingDetailRow?.referenceNumber?:"",
                subTitle = "Counting/Inception",
                onBack = {
                    onEvent(CountingInceptionContract.Event.OnBack)
                },
                endIcon = R.drawable.tick,
                onEndClick = {
                    onEvent(CountingInceptionContract.Event.OnShowConfirmDialog(true))
                }
            )
            Spacer(Modifier.size(20.mdp))
            val list = state.details.filterNot { it.entityState == "Deleted" }

            MyLazyColumn(
                items = list.reversed(),
                itemContent = {i,it->
                    CountingInceptionDetailItem(list.size-i,it,it == state.selectedItem){
                        onEvent(CountingInceptionContract.Event.OnSelectedItem(it,i))
                    }
                },
                header = {
                    Column {
                        if (state.countingDetailRow!=null){
                            CountingDetailItem(state.countingDetailRow){}
                        }
                        Spacer(Modifier.size(10.mdp))
                        Row(Modifier.fillMaxWidth()) {
                            InputTextField(
                                state.quantity,
                                onValueChange = {
                                    onEvent(CountingInceptionContract.Event.OnChangeQuantity(it))
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = R.drawable.box_search,
                                hideKeyboard = state.hideKeyboard,
                                label = "Quantity",
                            )
                            Spacer(Modifier.size(5.mdp))
                            InputTextField(
                                state.quantityInPacket,
                                onValueChange = {
                                    onEvent(CountingInceptionContract.Event.OnChangeQuantityInPacket(it))
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = R.drawable.barcode,
                                hideKeyboard = state.hideKeyboard,
                                label = "Quantity In Packet",
                            )
                        }
                        Spacer(Modifier.size(10.mdp))
                        InputTextField(
                            state.batchNumber,
                            onValueChange = {
                                onEvent(CountingInceptionContract.Event.OnChangeBatchNumber(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = R.drawable.keyboard,
                            hideKeyboard = state.hideKeyboard,
                            label = "Batch Number",
                        )
                        Spacer(Modifier.size(10.mdp))
                        InputTextField(
                            state.expireDate,
                            onValueChange = {
                                onEvent(CountingInceptionContract.Event.OnChangeExpireDate(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = R.drawable.calendar_add,
                            keyboardOptions = KeyboardOptions(),
                            onLeadingClick = {
                                onEvent(CountingInceptionContract.Event.OnShowDatePicker(true))
                            },
                            readOnly = true,
                            onClick = {
                                onEvent(CountingInceptionContract.Event.OnShowDatePicker(true))
                            },
                            label = "Expire Date",
                        )
                        Spacer(Modifier.size(10.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.mdp))
                                .background(Primary.copy(0.2f))
                                .clickable {
                                    onEvent(CountingInceptionContract.Event.OnAddClick)
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
                                clickable = false,
                            )
                        }
                        Spacer(Modifier.size(20.mdp))
                    }

                },
                onReachEnd = {},
                spacerSize = 5.mdp
            )
        }
    }
    if (state.showDatePicker) {
        DatePickerDialog(
            onDismiss = {
                onEvent(CountingInceptionContract.Event.OnShowDatePicker(false))
            },
            selectedDate = state.expireDate.text.ifEmpty { null }
        ) {
            onEvent(CountingInceptionContract.Event.OnChangeExpireDate(TextFieldValue(it)))
            onEvent(CountingInceptionContract.Event.OnShowDatePicker(false))

        }
    }
    if (state.selectedItem != null){
        ConfirmDialog(
            onDismiss = {
                onEvent(CountingInceptionContract.Event.OnSelectedItem(null))
            },
            onConfirm = {
                onEvent(CountingInceptionContract.Event.OnDeleteCount(state.selectedItem))
            }
        )
    }
    if (state.showConfirm) {
        ConfirmDialog(
            onDismiss = {
                onEvent(CountingInceptionContract.Event.OnShowConfirmDialog(false))
            },
            message = "Are you sure to count this items?",
            description = "",
            tint = Primary,
            onConfirm = {
                onEvent(CountingInceptionContract.Event.OnSubmit)
            }
        )
    }
}


@Composable
fun CountingInceptionDetailItem(
    i: Int,
    model: ReceivingDetailCountModel,
    selected: Boolean = false,
    onRemove: ()->Unit
) {
    DetailItem(
        i,
        first = model.batchNumber?:"",
        firstIcon = R.drawable.keyboard,
        second = model.quantity.toString(),
        third = model.expireDate?:"",
        onRemove = onRemove,
        selected = selected
    )
}



@Preview
@Composable
private fun CountingInceptionPreview() {
    CountingInceptionContent()
}