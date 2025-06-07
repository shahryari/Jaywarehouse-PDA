package com.example.jaywarehouse.presentation.picking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.data.picking.models.PickingListBDRow
import com.example.jaywarehouse.data.picking.models.PurchaseOrderDetailListBDRow
import com.example.jaywarehouse.data.picking.models.PurchaseOrderListBDRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
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
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayDetailContract
import com.example.jaywarehouse.presentation.picking.contracts.PickingDetailContract
import com.example.jaywarehouse.presentation.picking.contracts.PickingListBDContract
import com.example.jaywarehouse.presentation.picking.contracts.PurchaseOrderDetailContract
import com.example.jaywarehouse.presentation.picking.viewModels.PickingListBDViewModel
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.example.jaywarehouse.ui.theme.Green
import com.example.jaywarehouse.ui.theme.Orange
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
                    subTitle = "PickingBD",
                    onBack = {
                        onEvent(PickingListBDContract.Event.OnBackPressed)
                    },
                    endIcon = R.drawable.tick,
                    onEndClick = {
                        onEvent(PickingListBDContract.Event.OnShowFinishConfirm(true))
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                if (state.purchaseOrderDetailRow!=null){
                    val quantityDiff = state.purchaseOrderDetailRow.quantityDifferencePodandPIcks?:0.0
                    BaseListItem(
                        onClick = { },
                        item1 = BaseListItemModel("Name",state.purchaseOrderDetailRow.productName?:"", R.drawable.vuesax_outline_3d_cube_scan),
                        item2 = BaseListItemModel("Product Code",state.purchaseOrderDetailRow.productCode?:"",R.drawable.note),
                        item3 = BaseListItemModel("Barcode",state.purchaseOrderDetailRow.barcodeNumber?:"",R.drawable.barcode),
                        item4 = BaseListItemModel("PCB", state.purchaseOrderDetailRow.pcb?.toString()?:"",R.drawable.hashtag),
                        item5 = BaseListItemModel(
                            "Quantity Difference",
                            state.purchaseOrderDetailRow.quantityDifferencePodandPIcks?.removeZeroDecimal()?:"0",
                            R.drawable.vuesax_linear_card_remove,
                            MaterialTheme.typography.bodyMedium.copy(
                                color = if (quantityDiff < 0) Color(0xFFFF9800) else if (quantityDiff > 0) Color.Red else Green
                            )
                        ),
                        quantity = state.purchaseOrderDetailRow.quantity?.removeZeroDecimal()?:"",
                        quantityTitle = "Total",
                        scan = state.purchaseOrderDetailRow.sumReceiptQuantity?.removeZeroDecimal()?.toString()?:"",
                        scanTitle = "Quantity"
                    )
                }
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
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier=  Modifier.fillMaxSize(),
                    items = state.shippingOrderDetailList,
                    itemContent = {_,it->
                        PickingItem(it) {
                            onEvent(PickingListBDContract.Event.OnSelectShippingDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PickingListBDContract.Event.OnReachedEnd)
                    }
                )
            }
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
}

@Composable
fun PickingItem(model: PickingListBDRow,onClick: ()-> Unit) {
    BaseListItem(
        onClick = onClick,
        quantity = "",
        scan = "",
        showFooter = false,
        item1 = BaseListItemModel("Customer Name",model.customerName?:"",R.drawable.user_square),
        item2 = BaseListItemModel("Customer Code",model.customerCode?:"",R.drawable.note),
        item3 = BaseListItemModel("Reference Number",model.referenceNumber?:"",R.drawable.hashtag),
        item4 = BaseListItemModel("Quantity",model.splittedQuantity?.removeZeroDecimal()?:"",R.drawable.box_search)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifySheet(
    state: PickingListBDContract.State,
    onEvent: (PickingListBDContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
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
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedPicking.customerName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Customer Code",
                        icon = R.drawable.note,
                        detail = state.selectedPicking.customerCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Reference Number",
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
                        detail = state.selectedPicking.splittedQuantity?.removeZeroDecimal()?.toString()?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(PickingListBDContract.Event.OnQuantityChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.box_search,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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