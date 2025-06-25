package com.linari.presentation.cycle_count

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.cycle_count.models.CycleDetailRow
import com.linari.data.cycle_count.models.CycleRow
import com.linari.presentation.common.composables.AutoDropDownTextField
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DatePickerDialog
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.cycle_count.contracts.CycleDetailContract
import com.linari.presentation.cycle_count.viewmodels.CycleDetailViewModel
import com.linari.ui.theme.Black
import com.linari.ui.theme.Border
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Orange
import com.linari.ui.theme.Primary
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
    val searchFocusRequester = remember {
        FocusRequester()
    }

    val listState = rememberLazyListState()

    var lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CycleDetailContract.Event.FetchData)
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
        },
        onRefresh = {
            onEvent(CycleDetailContract.Event.OnRefresh)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {
            Column {
                Column(
                    Modifier
                        .weight(1f)
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
                    Spacer(modifier = Modifier.size(10.mdp))
                    SearchInput(
                        onSearch = {
                            onEvent(CycleDetailContract.Event.OnSearch(it.text))
                        },
                        value = state.keyword,
                        isLoading = state.loadingState == Loading.SEARCHING,
                        onSortClick = {
                            onEvent(CycleDetailContract.Event.OnShowSortList(true))
                        },
                        hideKeyboard = state.lockKeyboard,
                        focusRequester = searchFocusRequester
                    )
                    Spacer(modifier = Modifier.size(20.mdp))

                    if (state.cycleRow!=null)CycleItem(state.cycleRow, showCount = false)
                    Spacer(modifier = Modifier.size(15.mdp))
                    MyLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        items = state.details,
                        state = listState,
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
                Row(
                    Modifier
                        .shadow(1.mdp)
                        .fillMaxWidth()
                        .background(
                            Gray3
                        )
                        .padding(12.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    MyText(
                        text = "${lastItem.value.coerceAtMost(state.cycleDetailCount)}",
                        color = Primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
//                Icon(
//                    painter = painterResource(id = scanIcon),
//                    contentDescription = "",
//                    modifier = Modifier.size(28.mdp),
//                    tint = Color.White
//                )
//                Spacer(modifier = Modifier.size(7.mdp))
                    MyText(
                        text = " of ",
                        color = Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    MyText(
                        text = "${state.details.size}",
                        color = Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    MyText(
                        text = " from ",
                        color = Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    MyText(
                        text = "${state.cycleDetailCount}",
                        color = Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (state.details.isEmpty() && state.loadingState == Loading.NONE){
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painterResource(R.drawable.direct_normal),
                        contentDescription = "",
                        modifier = Modifier.size(139.mdp),
                        tint = Black.copy(0.3f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    MyText(
                        "No Data...",
                        fontWeight = FontWeight.W400,
                        color = Border,
                        fontSize = 14.sp
                    )
                }
            }

            if(state.showAddButton)Box(
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
        ) {f1,f2->
            onEvent(CycleDetailContract.Event.OnChangeExpireDate(TextFieldValue(f1)))
            onEvent(CycleDetailContract.Event.OnShowDatePicker(false))

        }
    }

    if(state.showSubmit) {
        ConfirmDialog(
            onDismiss = {
                onEvent(CycleDetailContract.Event.OnShowSubmit(false))
            },
            title = "Confirm",
            isLoading = state.isCompleting,
            message = "Are you sure to confirm finish counting of this location?",
            onConfirm = {
                onEvent(CycleDetailContract.Event.OnEndTaskClick)
            },
            tint = Orange
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
        item2 = BaseListItemModel("Product Code",model.productCode,R.drawable.note),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber,R.drawable.barcode),
        item4 = BaseListItemModel("Status", model.quiddityTypeTitle?:"",R.drawable.box_search),
        item5 = model.expireDate?.let { BaseListItemModel("Exp Date",it,R.drawable.calendar_add) },
        quantityTitle = "",
        primary = model.counting == 1,
        quantity = model.locationCode?:"",
        scanTitle = "Count",
        scan = model.countQuantity?.toString()?:""
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
    val focusRequester = remember {
        FocusRequester()
    }

    if (state.selectedCycle!=null) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
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
                    text = "Count Location",
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
                        "Product Code",
                        state.selectedCycle.productCode,
                        icon = R.drawable.note,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        "Barcode",
                        state.selectedCycle.productBarcodeNumber,
                        icon = R.drawable.barcode,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        "Status",
                        state.selectedCycle.quiddityTypeTitle?:"",
                        icon = R.drawable.box_search,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.selectedCycle.batchNumber!=null && state.selectedCycle.expireDate!=null)Spacer(Modifier.size(5.mdp))
                    if (state.selectedCycle.expireDate!=null)DetailCard(
                        "Exp Date",
                        state.selectedCycle.expireDate,
                        icon = R.drawable.note,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(15.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(CycleDetailContract.Event.OnChangeQuantity(it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = R.drawable.box_search,
                    focusRequester = focusRequester,
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
                        enabled = state.quantity.text.isNotEmpty(),
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
                    label = "Exp Date",
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
                        enabled = state.quantity.text.isNotEmpty() && state.barcode.text.isNotEmpty(),
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