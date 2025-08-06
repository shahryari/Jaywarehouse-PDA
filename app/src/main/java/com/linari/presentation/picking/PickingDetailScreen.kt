package com.linari.presentation.picking

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.picking.models.PickingListGroupedRow
import com.linari.data.picking.models.PickingListRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyIcon
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.RowCountView
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TitleView
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.destinations.DashboardScreenDestination
import com.linari.presentation.destinations.PutawayScreenDestination
import com.linari.presentation.picking.contracts.PickingDetailContract
import com.linari.presentation.picking.contracts.PickingListBDContract
import com.linari.presentation.picking.viewModels.PickingDetailViewModel
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun PickingDetailScreen(
    navigator: DestinationsNavigator,
    pickRow: PickingListGroupedRow,
    viewModel: PickingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(pickRow)
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
                PickingDetailContract.Effect.NavBack -> navigator.popBackStack()
                PickingDetailContract.Effect.NavToDashboard -> {
                    navigator.navigate(DashboardScreenDestination){
                        popUpTo(PutawayScreenDestination){
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
    PickingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickingDetailContent(
    state: PickingDetailContract.State = PickingDetailContract.State(),
    onEvent: (PickingDetailContract.Event)->Unit = {}
) {
    val focusRequester = FocusRequester()
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PickingDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(PickingDetailContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(PickingDetailContract.Event.OnRefresh)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.pickRow?.customerName?.trim()?:"",
                    subTitle = "Picking",
                    onBack = {
                        onEvent(PickingDetailContract.Event.OnNavBack)
                    },
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PickingDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PickingDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.pickingList,
                    itemContent = {_,it->
                        PickingDetailItem(
                            it,
                            hasWaste = state.hasWaste,
                            hasModify = state.hasModify,
                            onClick = {
                                onEvent(PickingDetailContract.Event.OnSelectPick(it))
                            },
                            onModify = {
                                onEvent(PickingDetailContract.Event.OnShowModify(it))
                            },
                            onWasteClick = {
                                onEvent(PickingDetailContract.Event.OnShowWaste(it))
                            }
                        )
                    },
                    onReachEnd = {
                        onEvent(PickingDetailContract.Event.OnReachEnd)
                    },
                    state = listState,
                    spacerSize = 7.mdp
                )
            }
            RowCountView(
                modifier = Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.pickingList.size,
                total = state.rowCount
            )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PickingDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PickingDetailContract.Event.OnSortChange(it))
            }
        )
    }
    PickingBottomSheet(state,onEvent)
    WasteSheet(state,onEvent)
    ModifySheet(state,onEvent)
}


@Composable
fun PickingDetailItem(
    model: PickingListRow,
    hasModify: Boolean,
    hasWaste: Boolean,
    onClick: ()->Unit,
    onModify: ()->Unit = {},
    onWasteClick:()->Unit = {}
) {
        BaseListItem(
            onClick = onClick,
            item1 = BaseListItemModel("Name",model.productName?:"", R.drawable.vuesax_outline_3d_cube_scan),
            item2 = BaseListItemModel("Product Code",model.productCode?:"",R.drawable.keyboard2),
            item3 = BaseListItemModel("Barcode",model.barcodeNumber?:"",R.drawable.barcode),
            item4 = BaseListItemModel("Reference No.", model.referenceNumber?:"",R.drawable.hashtag),
            item5 = if(model.typeofOrderAcquisition!=null) BaseListItemModel("Type of order acquisition", model.typeofOrderAcquisition,R.drawable.calendar_add)else null,
            item6 = BaseListItemModel("Location",model.warehouseLocationCode?:"",R.drawable.location),
            quantity = model.quantity.removeZeroDecimal() + if (model.isWeight == true) " kg" else "",
            quantityTitle = if (hasModify) "Modify" else "Quantity",
            quantityIcon = if(hasModify)R.drawable.edit else R.drawable.box_search,
            onQuantityClick = if (hasModify){{
                onModify()
            }} else null,
            scan = if (hasWaste) "" else null,
            scanTitle = "Waste",
            scanIcon = R.drawable.broom_outlined,
            onScanClick = if(hasWaste) {{
                onWasteClick()
            }} else null,
        )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingBottomSheet(
    state: PickingDetailContract.State,
    onEvent: (PickingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedPick!=null){

        val locationFocusRequester = remember {
            FocusRequester()
        }
        val barcodeFocusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(Unit) {
            if (!state.selectedPick.warehouseLocationCode.isNullOrEmpty())locationFocusRequester.requestFocus()
            else barcodeFocusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PickingDetailContract.Event.OnSelectPick(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Picking",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedPick.productName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.selectedPick.productCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.selectedPick.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference No.",
                        icon = R.drawable.hashtag,
                        detail = state.selectedPick.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))

                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedPick.quantity.removeZeroDecimal() + if (state.selectedPick.isWeight == true) " kg" else "",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    if (!state.selectedPick.warehouseLocationCode.isNullOrEmpty()){
                        DetailCard(
                            title = "Location",
                            icon = R.drawable.location,
                            detail = state.selectedPick.warehouseLocationCode?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                    }
                    if (state.selectedPick.typeofOrderAcquisition!=null)DetailCard(
                        title = "Type of order acquisition",
                        icon = R.drawable.calendar_add,
                        detail = state.selectedPick.typeofOrderAcquisition,
                        modifier = Modifier.weight(1f)
                    )

                }
                Spacer(Modifier.size(10.mdp))
                if (!state.selectedPick.warehouseLocationCode.isNullOrEmpty()){
                    TitleView(
                        title = "Location Code"
                    )
                    Spacer(Modifier.size(5.mdp))
                    InputTextField(
                        state.location,
                        onValueChange = {
                            onEvent(PickingDetailContract.Event.OnChangeLocation(it))
                        },
                        onAny = {
                            barcodeFocusRequester.requestFocus()
                        },
                        leadingIcon = R.drawable.location,
                        hideKeyboard = state.lockKeyboard,
                        focusRequester = locationFocusRequester,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(Modifier.size(10.mdp))
                }
                TitleView(title = "Barcode")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    onValueChange = {
                        onEvent(PickingDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PickingDetailContract.Event.OnSelectPick(null))
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

                            onEvent(PickingDetailContract.Event.OnCompletePick(state.selectedPick))
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
fun ModifySheet(
    state: PickingDetailContract.State,
    onEvent: (PickingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showModify!=null){
        val focusRequester = remember {
            FocusRequester()
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PickingDetailContract.Event.OnShowModify(null))
            },
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ) {
                MyText(
                    text = "Modify Picking",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Product Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.showModify?.productName?:"",
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.showModify?.productCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.showModify?.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Customer Name",
                        icon = R.drawable.vuesax_linear_user,
                        detail = state.showModify.customerName?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))

                    DetailCard(
                        title = "Customer Code",
                        icon = R.drawable.user_square,
                        detail = state.showModify.customerCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference No.",
                        icon = R.drawable.hashtag,
                        detail = state.showModify.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail =(state.showModify.quantity.removeZeroDecimal()) + if(state.showModify.isWeight == true) " kg" else "",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Quantity"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(PickingDetailContract.Event.ChangeQuantity(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.box_search,
                    suffix = if (state.showModify.isWeight == true) "kg" else "",
                    decimalInput = state.showModify.isWeight == true,
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PickingDetailContract.Event.OnShowModify(null))
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

                            onEvent(PickingDetailContract.Event.OnModifyPick(state.showModify))
                        },
                        title = "Save",
                        isLoading = state.isModifying,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteSheet(
    state: PickingDetailContract.State,
    onEvent: (PickingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showWaste!=null){
        val focusRequester = remember {
            FocusRequester()
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PickingDetailContract.Event.OnShowWaste(null))
            },
            containerColor = Color.White
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ) {
                MyText(
                    text = "Waste Picking",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Product Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.showWaste.productName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.showWaste.productCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.showWaste.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Customer Name",
                        icon = R.drawable.vuesax_linear_user,
                        detail = state.showWaste.customerName?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))

                    DetailCard(
                        title = "Customer Code",
                        icon = R.drawable.user_square,
                        detail = state.showWaste.customerCode,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference No.",
                        icon = R.drawable.hashtag,
                        detail = state.showWaste.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail =(state.showWaste.quantity.removeZeroDecimal()) + if(state.showWaste.isWeight == true) " kg" else "",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title  ="Waste Quantity"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(PickingDetailContract.Event.ChangeQuantity(it))
                    },
                    onAny = {},

                    suffix = if (state.showWaste.isWeight == true) "kg" else "",
                    decimalInput = state.showWaste.isWeight == true,
                    leadingIcon = R.drawable.box_search,
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PickingDetailContract.Event.OnShowWaste(null))
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

                            onEvent(PickingDetailContract.Event.OnWastePick(state.showWaste))
                        },
                        title = "Save",
                        isLoading = state.isWasting,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PickingDetailPreview() {
    PickingDetailContent(
        state = PickingDetailContract.State(
        )
    )

}