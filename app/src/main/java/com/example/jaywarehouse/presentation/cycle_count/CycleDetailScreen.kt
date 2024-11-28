package com.example.jaywarehouse.presentation.cycle_count

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailRow
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyButton
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.cycle_count.contracts.CycleDetailContract
import com.example.jaywarehouse.presentation.cycle_count.viewmodels.CycleDetailViewModel
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun CycleDetailScreen(
    navigator: DestinationsNavigator,
    row: CycleRow,
    viewModel: CycleDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(row)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
//    val localFocusManager = LocalFocusManager.current
//    val locationFocusRequester = remember {
//        FocusRequester()
//    }
//    val barcodeFocusRequester = remember {
//        FocusRequester()
//    }

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                CycleDetailContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    CycleDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CycleDetailContent(
    state: CycleDetailContract.State = CycleDetailContract.State(),
    onEvent: (CycleDetailContract.Event)->Unit = {}
) {
    val focusRequester = FocusRequester()

    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(CycleDetailContract.Event.OnRefresh) }
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CycleDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(CycleDetailContract.Event.HideToast)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {
            Column(
                Modifier
                    .pullRefresh(refreshState)
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = "Cycle Count",
                    subTitle = "Cycle Count",
                    onBack = {
                        onEvent(CycleDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeKeyword(it))
                    },
                    onSearch = {
                        onEvent(CycleDetailContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(CycleDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.details,
                    itemContent = {_,it->
                        LoadingDetailItem(it){
                            onEvent(CycleDetailContract.Event.OnSelectDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(CycleDetailContract.Event.OnReachEnd)
                    },
                    spacerSize = 7.mdp
                )
            }
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(
                Alignment.TopCenter) )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CycleDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CycleDetailContract.Event.OnSortChange(it))
            }
        )
    }

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismiss = {
                onEvent(CycleDetailContract.Event.OnShowDatePicker(false))
            },
            selectedDate = state.expireDate.text.ifEmpty { null }
        ) {
            onEvent(CycleDetailContract.Event.OnChangeExpireDate(TextFieldValue(it)))
            onEvent(CycleDetailContract.Event.OnShowDatePicker(false))

        }
    }
    AddBottomSheet(state,onEvent)
    CountBottomSheet(state,onEvent)
}


@Composable
fun LoadingDetailItem(
    model: CycleDetailRow,
    onClick: ()->Unit
) {

    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Product Name",model.productName?:"", R.drawable.vuesax_linear_box),
        item2 = BaseListItemModel("Barcode",model.productBarcodeNumber?:"",R.drawable.barcode),
        item3 = BaseListItemModel("Location",model.warehouseLocationCode?:"",R.drawable.location),
        showFooter = false,
        quantity = model.total,
        scan = model.count
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountBottomSheet(
    state: CycleDetailContract.State,
    onEvent: (CycleDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.selectedCycle!=null) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(CycleDetailContract.Event.OnSelectDetail(null))
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier

            ) {
                Row(Modifier.fillMaxWidth()) {
                    InputTextField(
                        state.quantity,
                        onValueChange = {
                            onEvent(CycleDetailContract.Event.OnChangeQuantity(it))
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
                            onEvent(CycleDetailContract.Event.OnChangeQuantityInPacket(it))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = R.drawable.barcode,
                        label = "Quantity In Packet",
                    )
                }
                Spacer(Modifier.size(10.mdp))
                InputTextField(
                    state.batchNumber,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeBatchNumber(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.keyboard,
                    label = "Batch Number",
                )
                Spacer(Modifier.size(10.mdp))
                InputTextField(
                    state.expireDate,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeExpireDate(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.calendar_add,
                    keyboardOptions = KeyboardOptions(),
                    onLeadingClick = {
                        onEvent(CycleDetailContract.Event.OnShowDatePicker(true))
                    },
                    readOnly = true,
                    onClick = {
                        onEvent(CycleDetailContract.Event.OnShowDatePicker(true))
                    },
                    label = "Expire Date",
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(CycleDetailContract.Event.OnSelectDetail(null))
                            }
                        },
                        title = "Cancel",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(CycleDetailContract.Event.OnSave(state.selectedCycle))
                        },
                        title = "Save",
                        isLoading = state.onSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
    state: CycleDetailContract.State,
    onEvent: (CycleDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.showAddDialog){
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(CycleDetailContract.Event.OnShowAddDialog(false))
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier

            ) {
                Row(Modifier.fillMaxWidth()) {
                    InputTextField(
                        state.quantityInPacket,
                        onValueChange = {
                            onEvent(CycleDetailContract.Event.OnChangeBarcode(it))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = R.drawable.barcode,
                        label = "Barcode",
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    InputTextField(
                        state.quantity,
                        onValueChange = {
                            onEvent(CycleDetailContract.Event.OnChangeQuantity(it))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = R.drawable.box_search,
                        label = "Quantity",
                    )
                }
                Spacer(Modifier.size(10.mdp))
                InputTextField(
                    state.batchNumber,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeBatchNumber(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.keyboard,
                    label = "Batch Number",
                )
                Spacer(Modifier.size(10.mdp))
                InputTextField(
                    state.expireDate,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeExpireDate(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.calendar_add,
                    keyboardOptions = KeyboardOptions(),
                    onLeadingClick = {
                        onEvent(CycleDetailContract.Event.OnShowDatePicker(true))
                    },
                    readOnly = true,
                    onClick = {
                        onEvent(CycleDetailContract.Event.OnShowDatePicker(true))
                    },
                    label = "Expire Date",
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(CycleDetailContract.Event.OnShowAddDialog(false))
                            }
                        },
                        title = "Cancel",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(CycleDetailContract.Event.OnAdd)
                        },
                        title = "Save",
                        isLoading = state.onSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}





@Preview
@Composable
private fun CheckingDetailPreview() {
    CycleDetailContent()

}