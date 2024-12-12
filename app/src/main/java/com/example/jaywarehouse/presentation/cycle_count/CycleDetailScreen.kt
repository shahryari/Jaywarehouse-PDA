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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailRow
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.DetailCard
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
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.presentation.cycle_count.contracts.CycleDetailContract
import com.example.jaywarehouse.presentation.cycle_count.viewmodels.CycleDetailViewModel
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.example.jaywarehouse.ui.theme.Primary
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

    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(CycleDetailContract.Event.OnRefresh) }
    )
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
                    subTitle = "Counting",
                    onBack = {
                        onEvent(CycleDetailContract.Event.OnNavBack)
                    },
                    endIcon = R.drawable.tick,
                    onEndClick = {
                        onEvent(CycleDetailContract.Event.OnShowSubmit(true))
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))

                if (state.cycleRow!=null)CycleItem(state.cycleRow)
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.details,
                    itemContent = {_,it->
                        CycleDetailItem(it){
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
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.mdp)
            ){
                FloatingActionButton(
                    onClick = {
                        onEvent(CycleDetailContract.Event.OnShowAddDialog(true))
                    },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Box(Modifier.padding(13.mdp)){
                        Icon(
                            painter = painterResource(R.drawable.add_square),
                            contentDescription = "",
                            modifier = Modifier.size(36.mdp),
                            tint = Color.White
                        )
                    }
                }
            }
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

    if(state.showSubmit) {
        ConfirmDialog(
            onDismiss = {
                onEvent(CycleDetailContract.Event.OnShowSubmit(false))
            },
            message = "Submit",
            description = "Are you sure you want to end this cycle count?",
            onConfirm = {
                onEvent(CycleDetailContract.Event.OnEndTaskClick)
            },
            tint = Primary
        )
    }
    AddBottomSheet(state,onEvent)
    CountBottomSheet(state,onEvent)
}


@Composable
fun CycleDetailItem(
    model: CycleDetailRow,
    onClick: ()->Unit
) {

    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productTitle,R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Status",model.quiddityTypeTitle,R.drawable.box_search),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber,R.drawable.note),
        item4 = if (model.batchNumber!=null)BaseListItemModel("Batch Number", model.batchNumber,R.drawable.keyboard2) else null,
        item5 = model.expireDate?.let { BaseListItemModel("Expiration Date",it,R.drawable.calendar_add) },
        quantityTitle = "",
        quantity = model.locationCode,
        scanTitle = "Quantity",
        scan = model.countQuantity?.toString()?:"0"
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
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)

            ) {

                MyText(
                    text = "Update",
                    fontSize = 16.sp,
                    color = Color(0xFF767676)
                )
                Spacer(Modifier.size(20.mdp))
                DetailCard(
                    "Name",
                    state.selectedCycle.productTitle,
                    icon = R.drawable.vuesax_outline_3d_cube_scan
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        "Status",
                        state.selectedCycle.quiddityTypeTitle?:"",
                        icon = R.drawable.box_search,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        "Barcode",
                        state.selectedCycle.productBarcodeNumber,
                        icon = R.drawable.note,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    if (state.selectedCycle.batchNumber!=null)DetailCard(
                        "Batch Number",
                        state.selectedCycle.batchNumber?:"",
                        icon = R.drawable.box_search,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.selectedCycle.batchNumber!=null && state.selectedCycle.expireDate!=null)Spacer(Modifier.size(5.mdp))
                    if (state.selectedCycle.expireDate!=null)DetailCard(
                        "Expiration Date",
                        state.selectedCycle.expireDate?:"",
                        icon = R.drawable.note,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.selectedCycle.batchNumber!= null || state.selectedCycle.expireDate!=null)Spacer(Modifier.size(10.mdp))
                DetailCard(
                    "Quantity",
                    state.selectedCycle.countQuantity?.toString()?:"0",
                    icon = R.drawable.vuesax_linear_box
                )
                Spacer(Modifier.size(15.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeQuantity(it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = R.drawable.box_search,
                    label = "Quantity",
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
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row {
                        MyText(
                            text = "Count [",
                            fontSize = 16.sp,
                            color = Color(0xFF767676)
                        )
                        MyText(
                            text = state.cycleRow?.locationCode?:"",
                            fontSize = 16.sp,
                            color = Primary
                        )
                        MyText(
                            text = "]",
                            fontSize = 16.sp,
                            color = Color(0xFF767676)
                        )
                    }
                }
                Spacer(Modifier.size(20.mdp))
                Row(Modifier.fillMaxWidth()) {


                    InputTextField(
                        state.barcode,
                        onValueChange = {
                            onEvent(CycleDetailContract.Event.OnChangeBarcode(it))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = R.drawable.barcode,
                        label = "Barcode",
                    )
                    Spacer(Modifier.size(8.mdp))
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
                AutoDropDownTextField(
                    state.status,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeStatus(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.keyboard2,
                    clickable = true,
                    suggestions = state.statusList,
                    onSuggestionClick = {
                        onEvent(CycleDetailContract.Event.OnSelectStatus(it))
                    },
                    label = "Status",
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