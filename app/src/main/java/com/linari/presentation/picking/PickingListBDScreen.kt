package com.linari.presentation.picking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.picking.models.PickingListBDRow
import com.linari.data.picking.models.PurchaseOrderDetailListBDRow
import com.linari.data.picking.models.PurchaseOrderListBDRow
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
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.manual_putaway.contracts.ManualPutawayDetailContract
import com.linari.presentation.picking.contracts.PickingDetailContract
import com.linari.presentation.picking.contracts.PickingListBDContract
import com.linari.presentation.picking.contracts.PurchaseOrderDetailContract
import com.linari.presentation.picking.viewModels.PickingListBDViewModel
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Green
import com.linari.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun PickingListBDScreen(
    navigator: DestinationsNavigator,
    purchase: PurchaseOrderListBDRow,
    purchaseDetail: PurchaseOrderDetailListBDRow,
    viewModel: PickingListBDViewModel = koinViewModel(
        parameters = {
            parametersOf(purchase,purchaseDetail)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PickingListBDContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    PickingListBDContent(state,onEvent)
}

@Composable
fun PickingListBDContent(
    state: PickingListBDContract.State = PickingListBDContract.State(),
    onEvent: (PickingListBDContract.Event)-> Unit = {}
){
    val focusRequester = remember {
        FocusRequester()
    }
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        onEvent(PickingListBDContract.Event.ReloadScreen)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PickingListBDContract.Event.ClearError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(PickingListBDContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(PickingListBDContract.Event.OnRefresh)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.purchaseOrderRow?.supplierName?.trim()?:"",
                    subTitle = "Picking",
                    onBack = {
                        onEvent(PickingListBDContract.Event.OnBackPressed)
                    },
                    endIcon = R.drawable.tick,
                    onEndClick = {
                        onEvent(PickingListBDContract.Event.OnShowFinishConfirm(true))
                    },
                    endIconEnabled = state.purchaseOrderDetailRow?.quantityDifferencePodandPIcks == 0.0
                )
                Spacer(Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PickingListBDContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PickingListBDContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )
                Spacer(modifier = Modifier.size(10.mdp))
                if (state.purchaseOrderDetailRow!=null){
                    BaseListItem(
                        onClick = { },
                        item1 = BaseListItemModel("Name",state.purchaseOrderDetailRow.productName?:"", R.drawable.vuesax_outline_3d_cube_scan),
                        item2 = BaseListItemModel("Product Code",state.purchaseOrderDetailRow.productCode?:"",R.drawable.keyboard2),
                        item3 = BaseListItemModel("Barcode",state.purchaseOrderDetailRow.barcodeNumber?:"",R.drawable.barcode),
                        item4 = BaseListItemModel("PCB", state.purchaseOrderDetailRow.pcb?.toString()?:"",R.drawable.hashtag),
                        item5 = BaseListItemModel("Waste Quantity",state.purchaseOrderDetailRow.wasteQuantity?.removeZeroDecimal()?:"0",R.drawable.broom_outlined),
                        item6 = BaseListItemModel(
                            "Difference Pick with PO",
                            state.purchaseOrderDetailRow.quantityDifferencePodandPIcks?.removeZeroDecimal()?:"_",
                            R.drawable.vuesax_linear_card_remove,
                            MaterialTheme.typography.bodyMedium.copy(
                                color = if (state.purchaseOrderDetailRow.quantityDifferencePodandPIcks == 0.0) Green else Color.Red
                            )
                        ),
                        quantity = (state.purchaseOrderDetailRow.sumReceiptQuantity?.removeZeroDecimal()?:"") + if (state.purchaseOrderDetailRow.isWeight==true) " kg" else "",
                        quantityTitle = "Total",
                        scan = (state.purchaseOrderDetailRow.sumPickingQty?.removeZeroDecimal() ?:"") + if (state.purchaseOrderDetailRow.isWeight == true) " kg" else "",
                        scanTitle = "Qty"
                    )
                }

                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier=  Modifier.fillMaxSize(),
                    items = state.shippingOrderDetailList,
                    itemContent = {_,it->
                        PickingItem(
                            it,
                            isWeight = state.purchaseOrderDetailRow?.isWeight == true,
                            hasWaste = state.hasWaste,
                            hasModify = state.hasModify,
                            onWasteClick = {
                                onEvent(PickingListBDContract.Event.OnSelectForWaste(it))
                            }
                        ) {
                            onEvent(PickingListBDContract.Event.OnSelectShippingDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PickingListBDContract.Event.OnReachedEnd)
                    },
                    state = listState
                )
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.shippingOrderDetailList.size,
                total = state.rowCount
            )
        }

    }

    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PickingListBDContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PickingListBDContract.Event.OnChangeSort(it))
            }
        )
    }
    if(state.showConfirmFinish){
        ConfirmDialog(
            onDismiss = {
                onEvent(PickingListBDContract.Event.OnShowFinishConfirm(false))
            },
            onConfirm = {
                onEvent(PickingListBDContract.Event.OnFinish)
            },
            message = "Are you sure to finish this?",
            isLoading = state.isFinishing,
            tint = Orange,
            title = "Confirm"
        )
    }
    ModifySheet(state,onEvent)
    WasteSheet(state,onEvent)
}

@Composable
fun PickingItem(model: PickingListBDRow,isWeight: Boolean,hasWaste: Boolean,hasModify: Boolean,onWasteClick:()->Unit,onClick: ()-> Unit) {
    var expended by remember {
        mutableStateOf(false)
    }
    BaseListItem(
        onClick = {
            expended=!expended
        },
        quantity = (model.splittedQuantity?.removeZeroDecimal()?:"0")+if (isWeight) " kg" else "",
        quantityTitle = if (hasModify) "Modify" else "Qty",
        quantityIcon = if(hasModify)R.drawable.edit else R.drawable.box_search,
        onQuantityClick = if (hasModify){{
            onClick()
        }} else null,
        scan = if (hasWaste) "" else null,
        scanTitle = "Waste",
        scanIcon = R.drawable.broom_outlined,
        onScanClick = if(hasWaste) {{
            onWasteClick()
        }} else null,
        showFooter = true,
        item1 = BaseListItemModel("Customer Name",model.customerName?:"",R.drawable.vuesax_linear_user),
        item2 = if (expended)BaseListItemModel("Customer Code",model.customerCode?:"",R.drawable.user_square) else null,
        item3 = if (expended)BaseListItemModel("Reference No.",model.referenceNumber?:"",R.drawable.hashtag) else null,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifySheet(
    state: PickingListBDContract.State,
    onEvent: (PickingListBDContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.selectedPicking!=null){
        val focusRequester = remember {
            FocusRequester()
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PickingListBDContract.Event.OnSelectShippingDetail(null))
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
                    title = "Customer Name",
                    icon = R.drawable.vuesax_linear_user,
                    detail = state.selectedPicking.customerName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Customer Code",
                        icon = R.drawable.user_square,
                        detail = state.selectedPicking.customerCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Reference No.",
                        icon = R.drawable.hashtag,
                        detail = state.selectedPicking.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {

                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail = (state.selectedPicking.splittedQuantity?.removeZeroDecimal() ?:"") + if (state.purchaseOrderDetailRow?.isWeight == true) " kg" else "",
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
                        onEvent(PickingListBDContract.Event.OnQuantityChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.box_search,
                    suffix = if (state.purchaseOrderDetailRow?.isWeight == true) "kg" else "",
                    decimalInput = state.purchaseOrderDetailRow?.isWeight == true ,
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PickingListBDContract.Event.OnSelectShippingDetail(null))
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

                            onEvent(PickingListBDContract.Event.OnModify)
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
    state: PickingListBDContract.State,
    onEvent: (PickingListBDContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.selectedForWaste!=null){
        val focusRequester = remember {
            FocusRequester()
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PickingListBDContract.Event.OnSelectForWaste(null))
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
                    detail = state.purchaseOrderDetailRow?.productName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.purchaseOrderDetailRow?.productCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.purchaseOrderDetailRow?.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Customer Name",
                        icon = R.drawable.vuesax_linear_user,
                        detail = state.selectedForWaste.customerName?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))

                    DetailCard(
                        title = "Customer Code",
                        icon = R.drawable.user_square,
                        detail = state.selectedForWaste.customerCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference No.",
                        icon = R.drawable.hashtag,
                        detail = state.selectedForWaste.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail =(state.selectedForWaste.splittedQuantity?.removeZeroDecimal() ?:"") + if(state.purchaseOrderDetailRow?.isWeight == true) " kg" else "",
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
                        onEvent(PickingListBDContract.Event.OnQuantityChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.box_search,
                    suffix =if (state.purchaseOrderDetailRow?.isWeight == true) "kg" else "",
                    decimalInput = state.purchaseOrderDetailRow?.isWeight == true,
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PickingListBDContract.Event.OnSelectForWaste(null))
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

                            onEvent(PickingListBDContract.Event.OnWaste(state.selectedForWaste))
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