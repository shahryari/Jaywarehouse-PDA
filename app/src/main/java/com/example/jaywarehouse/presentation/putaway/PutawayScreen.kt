package com.example.jaywarehouse.presentation.putaway

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
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
import com.example.jaywarehouse.presentation.destinations.PutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayContract
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayViewModel
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PutawayScreen(
    navigator: DestinationsNavigator,
    viewModel: PutawayViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is PutawayContract.Effect.NavToPutawayDetail -> {
                    navigator.navigate(PutawayDetailScreenDestination(it.readyToPutRow,it.fillLocation))
                }
            }
        }
    }
    PutawayContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PutawayContent(
    state: PutawayContract.State = PutawayContract.State(),
    onEvent: (PutawayContract.Event)->Unit = {}
) {
    val sortList = mapOf("Model" to "Model", "Barcode" to "Barcode","Created On" to "CreatedOn","Location" to "Location")
    val searchRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState ==Loading.REFRESHING,
        onRefresh = {
            onEvent(PutawayContract.Event.OnRefresh)
        }
    )
    LaunchedEffect(key1 = Unit) {
        searchRequester.requestFocus()
        onEvent(PutawayContract.Event.ReloadScreen)

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
                    text = stringResource(id = R.string.putaway),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(PutawayContract.Event.OnChangeKeyword(it))
                    },
                    onSearch = {
                        onEvent(PutawayContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PutawayContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.puts){
                        PutawayItem(it, onClick = {
                            onEvent(PutawayContract.Event.OnNavToPutawayDetail(it))
                        })
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(PutawayContract.Event.OnReachedEnd)
                    }
                }
                Spacer(modifier = Modifier.size(80.mdp))
            }
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(Modifier.align(Alignment.Center))
            PullRefreshIndicator(state.loadingState == Loading.REFRESHING, refreshState, Modifier.align(Alignment.TopCenter))
        }
    }
    if (state.error.isNotEmpty()){
        ErrorDialog(
            onDismiss = {
                onEvent(PutawayContract.Event.ClearError)
            },
            message = state.error
        )
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PutawayContract.Event.OnShowSortList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PutawayContract.Event.OnChangeSort(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(PutawayContract.Event.OnChangeOrder(it))
            }
        )
    }
}


@Composable
fun PutawayItem(
    model: ReadyToPutRow,
    enableShowDetail: Boolean = false,
    showAll: Boolean = true,
    onClick:()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item2 = BaseListItemModel("Location Code",model.locationCode, R.drawable.location),
        item3 = BaseListItemModel("Barcode",model.barcode,R.drawable.fluent_barcode_scanner_20_regular),
        item4 = if (showAll)BaseListItemModel("Model",model.model, R.drawable.vuesax_outline_3d_cube_scan) else null,
        item6 = if (showAll) BaseListItemModel("Ref Number",model.referenceNumber?:"", R.drawable.note) else null,
        enableShowDetail = enableShowDetail,
        quantity = model.quantity,
        scan = model.putCount
    )
}

@Preview
@Composable
private fun PoutawayPreview() {
    PutawayContent()
}