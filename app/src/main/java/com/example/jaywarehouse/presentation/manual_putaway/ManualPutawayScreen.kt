package com.example.jaywarehouse.presentation.manual_putaway

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.ManualPutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayContract
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel


@Destination(style = ScreenTransition::class)
@Composable
fun ManualPutawayScreen(
    navigator: DestinationsNavigator,
    viewModel: ManualPutawayViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it) {
                ManualPutawayContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
                is ManualPutawayContract.Effect.NavToPutawayDetail -> {
                    navigator.navigate(ManualPutawayDetailScreenDestination(it.putaway))
                }
            }
        }
    }
    ManualPutawayContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManualPutawayContent(
    state: ManualPutawayContract.State = ManualPutawayContract.State(),
    onEvent: (ManualPutawayContract.Event) -> Unit = {}
) {
    val focusRequester = FocusRequester()

    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(ManualPutawayContract.Event.OnReloadScreen) }
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ManualPutawayContract.Event.OnCloseError)
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
                    title = "Manual Putaway",
                    onBack = {
                        onEvent(ManualPutawayContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(ManualPutawayContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(ManualPutawayContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ManualPutawayContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.putaways){
                        ManualPutawayItem(it){
                            onEvent(ManualPutawayContract.Event.OnPutawayClick(it))
                        }
                        Spacer(modifier = Modifier.size(7.mdp))
                    }
                    item {
                        onEvent(ManualPutawayContract.Event.OnReachEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(70.mdp))
                    }
                }
            }
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(
                Alignment.TopCenter) )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ManualPutawayContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.selectedSort,
            onSelectSort = {
                onEvent(ManualPutawayContract.Event.OnSortChange(it))
            }
        )
    }

}


@Composable
fun ManualPutawayItem(
    model: ManualPutawayRow,
    onClick: ()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode, R.drawable.barcode),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber, R.drawable.note),
        item4 = BaseListItemModel("Batch Number",model.batchNumber?:"", R.drawable.vuesax_linear_box),
        item5 = BaseListItemModel("Expiration Date",model.expireDateString?:"", R.drawable.calendar_add),
        quantity = model.warehouseLocationCode?:"",
        quantityTitle = "Location",
        scan = model.quantity.toString(),
        scanTitle = "Quantity"
    )
}

@Preview
@Composable
private fun ManualPutawayPreview() {
    ManualPutawayContent()
}