package com.example.jaywarehouse.presentation.counting

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.DetailHeader
import com.example.jaywarehouse.presentation.common.composables.DetailItem
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingInceptionViewModel
import com.example.jaywarehouse.ui.theme.Orange
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
    isCrossDock: Boolean = false,
    viewModel: CountingInceptionViewModel = koinViewModel(
        parameters = {
            parametersOf(
                detail,
                receivingId,
                isCrossDock
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
    var boxVisibility by remember {
        mutableStateOf(true)
    }
    var showDetail by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(state.quantity,state.quantityInPacket) {
        if (state.countingDetailRow?.isWeight == false) {
            val pcb = state.quantityInPacket.text.toDoubleOrNull()?:1.0
            val quantity = state.quantity.text.toDoubleOrNull()?:0.0
            if (pcb>1.0) {
                boxVisibility = true
                onEvent(CountingInceptionContract.Event.OnChangeBoxQuantity(
                    TextFieldValue((quantity/pcb).toInt().toString())
                ))
            } else {
                boxVisibility = false
            }
        }
    }
    MyScaffold(
        error = state.error,
        onCloseError = {
            onEvent(CountingInceptionContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(CountingInceptionContract.Event.HideToast)
        },
        loadingState = state.loadingState,
        onRefresh = {
            onEvent(CountingInceptionContract.Event.OnRefresh)
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
                endIconEnabled = state.details.isNotEmpty(),
                onEndClick = {
                    onEvent(CountingInceptionContract.Event.OnShowConfirmDialog(true))
                }
            )
            Spacer(Modifier.size(20.mdp))

            if (state.countingDetailRow?.isWeight == true){
                MyLazyColumn(
                    items = state.details,
                    itemContent = {i,it->
                        CountingInceptionDetailItem(state.details.size-i,it,it == state.selectedItem){
                            onEvent(CountingInceptionContract.Event.OnSelectedItem(it))
                        }
                    },
                    header = {
                        Column {
                            CountingDetailItem(state.countingDetailRow, showDetail = showDetail){
                                showDetail = !showDetail
                            }
                            Spacer(Modifier.size(10.mdp))

                            Row(Modifier.fillMaxWidth()) {
                                InputTextField(
                                    state.quantityInPacket,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeQuantityInPacket(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.barcode,
                                    required = true,
                                    enabled = state.pcbEnabled,
                                    label = "Pcb",
                                )
                                Spacer(Modifier.size(7.mdp))
                                InputTextField(
                                    state.quantity,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeQuantity(it))
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.box_search,
                                    decimalInput = true,
                                    required = true,
                                    modifier = Modifier.weight(1f),
                                    suffix = "Kg",
                                    label = "Weight",
                                )
                            }
                            Spacer(Modifier.size(7.mdp))
                            InputTextField(
                                state.boxQuantity,
                                onValueChange = {
                                    onEvent(CountingInceptionContract.Event.OnChangeBoxQuantity(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                leadingIcon = R.drawable.vuesax_linear_box,
                                required = true,
                                label = "Box Quantity",
                            )
                            if (state.details.isEmpty())Spacer(Modifier.size(10.mdp))
                            AnimatedVisibility(state.details.isEmpty()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.mdp))
                                        .background(Primary.copy(0.2f))
                                        .clickable {
                                            onEvent(CountingInceptionContract.Event.OnAddWeight)
                                        }
                                        .border(1.mdp, Primary, RoundedCornerShape(6.mdp))
                                        .padding(9.mdp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (state.isAdding) CircularProgressIndicator(
                                        modifier = Modifier.size(24.mdp)
                                    ) else {
                                        MyIcon(
                                            icon = Icons.Default.Add, showBorder = false,
                                            background = Color.Transparent,
                                            tint = Primary,
                                            clickable = false,
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.size(20.mdp))

                            AnimatedVisibility(state.details.isNotEmpty()) {
                                DetailHeader(
                                    "Weight",
                                    "",
                                    "",
                                    ""
                                )
                            }

                            Spacer(Modifier.size(5.mdp))
                        }

                    },
                    onReachEnd = {
                        onEvent(CountingInceptionContract.Event.OnReachEnd)
                    },
                    spacerSize = 5.mdp
                )
            } else {
                MyLazyColumn(
                    items = state.details,
                    itemContent = {i,it->
                        CountingInceptionDetailItem(state.details.size-i,it,it == state.selectedItem){
                            onEvent(CountingInceptionContract.Event.OnSelectedItem(it))
                        }
                    },
                    header = {
                        Column {
                            if (state.countingDetailRow!=null){
                                CountingDetailItem(state.countingDetailRow, showDetail = showDetail){
                                    showDetail = !showDetail
                                }                            }
                            Spacer(Modifier.size(10.mdp))
                            Row(Modifier.fillMaxWidth()) {
                                InputTextField(
                                    state.quantityInPacket,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeQuantityInPacket(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.barcode,
                                    required = true,
                                    enabled = state.pcbEnabled,
                                    label = "Pcb",
                                )
                                Spacer(Modifier.size(7.mdp))
                                InputTextField(
                                    state.quantity,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeQuantity(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.box_search,
                                    required = true,
                                    label = "Quantity",
                                )
                            }
                            Spacer(Modifier.size(7.mdp))
                            Row(Modifier.fillMaxWidth()) {
                                InputTextField(
                                    state.boxQuantity,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeBoxQuantity(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.vuesax_linear_box,
                                    required = true,
                                    enabled = boxVisibility,
                                    label = "Box Quantity",
                                )
//                                Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.size(7.mdp))
                            Row(Modifier.fillMaxWidth()) {
                                InputTextField(
                                    state.expireDate,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeExpireDate(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    leadingIcon = R.drawable.calendar_add,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    onLeadingClick = {
                                        onEvent(CountingInceptionContract.Event.OnShowDatePicker(true))
                                    },
                                    readOnly = true,
                                    onClick = {
                                        onEvent(CountingInceptionContract.Event.OnShowDatePicker(true))
                                    },
                                    label = "Exp Date",
                                )
                                Spacer(Modifier.size(7.mdp))
                                InputTextField(
                                    state.batchNumber,
                                    onValueChange = {
                                        onEvent(CountingInceptionContract.Event.OnChangeBatchNumber(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.keyboard,
//                                    hideKeyboard = state.hideKeyboard,
                                    label = "Batch No.",
                                )
                            }
                            Spacer(Modifier.size(10.mdp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.mdp))
                                    .background(Primary.copy(0.2f))
                                    .clickable(!state.isAdding) {
                                        onEvent(CountingInceptionContract.Event.OnAddClick)
                                    }
                                    .border(1.mdp, Primary, RoundedCornerShape(6.mdp))
                                    .padding(9.mdp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (state.isAdding) CircularProgressIndicator(
                                    modifier = Modifier.size(24.mdp)
                                ) else {
                                    MyIcon(
                                        icon = Icons.Default.Add, showBorder = false,
                                        background = Color.Transparent,
                                        tint = Primary,
                                        clickable = false,
                                    )
                                }
                            }
                            Spacer(Modifier.size(20.mdp))
                            AnimatedVisibility(state.details.isNotEmpty()) {
                                DetailHeader(
                                    "Qty",
                                    "Pack",
                                    "Batch No.",
                                    "Exp Date"
                                )
                            }

                            Spacer(Modifier.size(5.mdp))
                        }

                    },
                    onReachEnd = {
                        onEvent(CountingInceptionContract.Event.OnReachEnd)
                    },
                    spacerSize = 5.mdp
                )
            }

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
            isLoading = state.isDeleting,
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
            title = "Confirm",
            isLoading = state.isCompleting,
            tint = Orange,
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
        first = model.countQuantity.toString(),
        second = model.pack?.toString()?:"",
        third = model.batchNumber?:"",
        forth = model.expireDate?:"",
        onRemove = onRemove,
        selected = selected
    )
}



@Preview
@Composable
private fun CountingInceptionPreview() {
    CountingInceptionContent()
}