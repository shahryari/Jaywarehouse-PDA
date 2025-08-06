package com.linari.presentation.picking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.MainListItem
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.RowCountView
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.destinations.PurchaseOrderDetailScreenDestination
import com.linari.presentation.picking.contracts.PurchaseOrderContract
import com.linari.presentation.picking.viewModels.PurchaseOrderViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun PurchaseOrderScreen(
    navigator: DestinationsNavigator,
    viewModel: PurchaseOrderViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PurchaseOrderContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
                is PurchaseOrderContract.Effect.NavToPurchaseOrderDetail -> {
                    navigator.navigate(PurchaseOrderDetailScreenDestination(it.purchase))
                }
            }
        }
    }
    PurchaseOrderContent(state,onEvent)
}


@Composable
fun PurchaseOrderContent(
    state: PurchaseOrderContract.State = PurchaseOrderContract.State(),
    onEvent: (PurchaseOrderContract.Event)-> Unit
) {

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
        onEvent(PurchaseOrderContract.Event.ReloadScreen)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PurchaseOrderContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(PurchaseOrderContract.Event.OnRefresh)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    "Picking",
                    onBack = {
                        onEvent(PurchaseOrderContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PurchaseOrderContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PurchaseOrderContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier=  Modifier.fillMaxSize(),
                    items = state.purchaseOrderList,
                    itemContent = {_,it->
                        PurchaseItem(it) {
                            onEvent(PurchaseOrderContract.Event.OnPurchaseClick(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PurchaseOrderContract.Event.OnReachedEnd)
                    },
                    state = listState
                )
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.purchaseOrderList.size,
                total = state.rowCount
            )
        }

    }

    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PurchaseOrderContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PurchaseOrderContract.Event.OnChangeSort(it))
            }
        )
    }

}

@Composable
fun PurchaseItem(
    model: PurchaseOrderListBDRow,
    onClick: ()-> Unit
) {
    MainListItem(
        onClick = onClick,
        typeTitle = model.purchaseOrderDate,
        modelNumber = model.referenceNumber,
        item1 = BaseListItemModel("Supplier Name",model.supplierName?:"",R.drawable.user_square),
        item2 = BaseListItemModel("Supplier Code",model.supplierCode?:"",R.drawable.vuesax_linear_box),
        total = model.count?.removeZeroDecimal()?:"",
        totalTitle = "Total",
        count = model.total?.removeZeroDecimal()?:"",
        countTitle = "Qty"
    )
}