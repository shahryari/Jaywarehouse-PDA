package com.example.jaywarehouse.presentation.picking.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.data.picking.models.ReadyToPickRow
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
import com.example.jaywarehouse.presentation.destinations.PickingDetailScreenDestination
import com.example.jaywarehouse.presentation.picking.contracts.PickingListContract
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PickingListScreen(
    navigator: DestinationsNavigator,
    customer: CustomerToPickRow,
    viewModel: PickingListViewModel = koinViewModel(
        parameters = {
            parametersOf(customer)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PickingListContract.Effect.NavigateBack -> {
                    navigator.popBackStack()
                }
                is PickingListContract.Effect.NavigateToPickingDetail -> {
                    navigator.navigate(
                        PickingDetailScreenDestination(
                            pickingRow = it.readyToPickRow,
                            customer = customer,
                            it.fillLocation
                        )
                    )
                }
            }
        }
    }

    PickingListContent(state = state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PickingListContent(
    state: PickingListContract.State,
    onEvent: (PickingListContract.Event)->Unit
) {
    val sortList = mapOf("Product Code" to "ProductCode", "Location Code" to "LocationCode","Created On" to "CreatedOn","Product Name" to "ProductName")
    val searchFocusRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PickingListContract.Event.OnRefresh)
        }
    )
    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PickingListContract.Event.FetchData)
    }
    MyScaffold(loadingState = state.loadingState) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.mdp))
                        .background(Color.Black.copy(0.85f))
                        .clickable {
                            onEvent(PickingListContract.Event.OnNavBack)
                        }
                        .padding(5.mdp)){
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(26.mdp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    MyText(
                        "Picking List of ${state.customer?.customerName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(PickingListContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(PickingListContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PickingListContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.pickingList){
                        PickingListItem(row = it) {
                            onEvent(PickingListContract.Event.OnPickClick(it))
                        }
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(PickingListContract.Event.OnReachToEnd)
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
                onEvent(PickingListContract.Event.ClearError)
            },
            message = state.error
        )
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PickingListContract.Event.OnShowSortList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PickingListContract.Event.OnSortChanged(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(PickingListContract.Event.OnOrderChanged(it))
            }
        )
    }
}

@Composable
fun PickingListItem(
    row: ReadyToPickRow,
    onClick:()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Model",row.model, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Item Code",row.barcode,R.drawable.fluent_barcode_scanner_20_regular),
        item3 = BaseListItemModel("Location Code",row.locationCode,R.drawable.location),
        quantity = row.quantity,
        scan = row.scanCount?:0
    )
}

@Preview
@Composable
private fun PickingListPreview() {
    MyScaffold {
        PickingListContent(state = PickingListContract.State(),{})
    }
}