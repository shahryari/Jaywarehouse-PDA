package com.example.jaywarehouse.presentation.picking.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.PickingListScreenDestination
import com.example.jaywarehouse.presentation.picking.contracts.PickingCustomerContract
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingCustomerViewModel
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PickingCustomerScreen(
    navigator: DestinationsNavigator,
    viewModel: PickingCustomerViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PickingCustomerContract.Effect.NavigateBack -> {
                    navigator.popBackStack()
                }
                is PickingCustomerContract.Effect.NavigateToPicking -> {
                    navigator.navigate(PickingListScreenDestination(it.customerToPickRow))
                }
            }
        }

    }
    PickingCustomerContent(state = state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickingCustomerContent(
    state: PickingCustomerContract.State,
    onEvent: (PickingCustomerContract.Event) -> Unit
) {
    val sortList = mapOf("Model" to "Model", "Barcode" to "Barcode","Created On" to "CreatedOn","Location" to "Location")
    val searchFocusRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PickingCustomerContract.Event.OnRefresh)
        }
    )
    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PickingCustomerContract.Event.FetchData)
        
    }
    MyScaffold(offset = (-70).mdp, loadingState = state.loadingState) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {

                MyText(
                    text = "Picking Customer",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(PickingCustomerContract.Event.OnKeyWordChange(it))
                    },
                    onSearch = {
                        onEvent(PickingCustomerContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PickingCustomerContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.customerToPicks){
                        PickingCustomerItem(row = it){
                            onEvent(PickingCustomerContract.Event.OnPickClick(it))
                        }
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(PickingCustomerContract.Event.OnReachEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(80.mdp))
                    }
                }
            }
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(Modifier.align(Alignment.Center))

            PullRefreshIndicator(
                refreshing = state.loadingState == Loading.REFRESHING,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    if (state.error.isNotEmpty()){
        ErrorDialog(
            onDismiss = {
                onEvent(PickingCustomerContract.Event.ClearError)
            },
            message = state.error
        )
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PickingCustomerContract.Event.OnShowSortList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PickingCustomerContract.Event.OnSortChanged(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(PickingCustomerContract.Event.OnOrderChanged(it))
            }
        )
    }
}

@Composable
fun PickingCustomerItem(
    row:CustomerToPickRow,
    onClick:()->Unit,
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Customer", row.customerName,R.drawable.vuesax_linear_user),
        item2 = BaseListItemModel("Customer Code",row.customerCode,R.drawable.hashtag),
//        item3 = BaseListItemModel("Created On",row.customerID.toString(),R.drawable.vuesax_linear_calendar_2),
        quantity = row.sumQuantity,
        scan = row.sumPickedQty?:0
    )
}

@Preview
@Composable
private fun PickingCustomerPreview() {
    MyScaffold {
        PickingCustomerContent(PickingCustomerContract.State(),{})
    }
}