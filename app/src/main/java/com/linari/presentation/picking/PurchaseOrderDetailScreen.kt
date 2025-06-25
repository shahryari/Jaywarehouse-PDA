package com.linari.presentation.picking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.picking.models.PurchaseOrderDetailListBDRow
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.destinations.PickingListBDScreenDestination
import com.linari.presentation.picking.contracts.PurchaseOrderDetailContract
import com.linari.presentation.picking.viewModels.PurchaseOrderDetailViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun PurchaseOrderDetailScreen(
    navigator: DestinationsNavigator,
    purchase: PurchaseOrderListBDRow,
    viewModel: PurchaseOrderDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(purchase)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PurchaseOrderDetailContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
                is PurchaseOrderDetailContract.Effect.NavToShippingOrderDetail -> {
                    navigator.navigate(PickingListBDScreenDestination(purchase,it.purchase
                    ))
                }
            }
        }
    }
    PurchaseOrderDetailContent(state,onEvent)
}

@Composable
fun PurchaseOrderDetailContent(
    state: PurchaseOrderDetailContract.State = PurchaseOrderDetailContract.State(),
    onEvent: (PurchaseOrderDetailContract.Event)-> Unit = {}
){
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        onEvent(PurchaseOrderDetailContract.Event.ReloadScreen)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PurchaseOrderDetailContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(PurchaseOrderDetailContract.Event.OnRefresh)
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
                        onEvent(PurchaseOrderDetailContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PurchaseOrderDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PurchaseOrderDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier=  Modifier.fillMaxSize(),
                    items = state.purchaseOrderDetailList,
                    itemContent = {_,it->
                        PurchaseDetailItem(it) {
                            onEvent(PurchaseOrderDetailContract.Event.OnPurchaseDetailClick(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PurchaseOrderDetailContract.Event.OnReachedEnd)
                    }
                )
            }
        }

    }

    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PurchaseOrderDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PurchaseOrderDetailContract.Event.OnChangeSort(it))
            }
        )
    }
}


@Composable
fun PurchaseDetailItem(
    model: PurchaseOrderDetailListBDRow,
    onClick: ()-> Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName?:"", R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode?:"",R.drawable.note),
        item3 = BaseListItemModel("Barcode",model.barcodeNumber?:"",R.drawable.barcode),
        item4 = BaseListItemModel("PCB", model.pcb?.toString()?:"",R.drawable.hashtag),
        quantity = model.sumReceiptQuantity?.removeZeroDecimal()?:"",
        quantityTitle = "Quantity",
        scan = model.sumPickingQty?.removeZeroDecimal()?.toString()?:"",
        scanTitle = "Scan",
    )
}