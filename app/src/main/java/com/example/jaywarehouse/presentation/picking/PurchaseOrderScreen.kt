package com.example.jaywarehouse.presentation.picking

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
import androidx.compose.ui.res.stringResource
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.picking.models.PurchaseOrderListBDRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.MainListItem
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.PurchaseOrderDetailScreenDestination
import com.example.jaywarehouse.presentation.picking.contracts.PurchaseOrderContract
import com.example.jaywarehouse.presentation.picking.viewModels.PurchaseOrderViewModel
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
                    "PickingBD",
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
                    }
                )
            }
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
        showFooter = false
    )
}