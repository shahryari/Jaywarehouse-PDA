package com.example.jaywarehouse.presentation.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
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
import com.example.jaywarehouse.presentation.loading.contracts.LoadingDetailContract
import com.example.jaywarehouse.presentation.loading.viewmodels.LoadingDetailViewModel
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun LoadingDetailScreen(
    navigator: DestinationsNavigator,
    row: LoadingListGroupedRow,
    viewModel: LoadingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(row)
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
                LoadingDetailContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    LoadingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingDetailContent(
    state: LoadingDetailContract.State = LoadingDetailContract.State(),
    onEvent: (LoadingDetailContract.Event)->Unit = {}
) {
    val focusRequester = FocusRequester()

    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(LoadingDetailContract.Event.OnRefresh) }
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(LoadingDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(LoadingDetailContract.Event.HideToast)
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
                    title = state.loadingRow?.customerName?.trim()?:"",
                    subTitle = "Loading",
                    onBack = {
                        onEvent(LoadingDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(LoadingDetailContract.Event.OnChangeKeyword(it))
                    },
                    onSearch = {
                        onEvent(LoadingDetailContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(LoadingDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.details,
                    itemContent = {_,it->
                        LoadingDetailItem(it){
                            onEvent(LoadingDetailContract.Event.OnSelectDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(LoadingDetailContract.Event.OnReachEnd)

                    },
                    spacerSize = 7.mdp
                )
            }
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(
                Alignment.TopCenter) )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(LoadingDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(LoadingDetailContract.Event.OnSortChange(it))
            }
        )
    }
    if (state.selectedLoading!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(LoadingDetailContract.Event.OnSelectDetail(null))
            },
            message = "Confirm Loading",
            description = "Are you sure to confirm this loading?",
            tint = Primary
        ) {
            onEvent(LoadingDetailContract.Event.OnConfirmLoading(state.selectedLoading))
        }
    }
}


@Composable
fun LoadingDetailItem(
    model: PalletConfirmRow,
    onSelect: ()->Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onSelect()
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
            true
        }
    )
    LaunchedEffect(swipeState.currentValue) {
        swipeState.reset()
    }
    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {}
    ) {
        Row(
            Modifier.fillMaxWidth()
                .shadow(1.mdp)
                .clip(RoundedCornerShape(6.mdp))
                .background(Color.White)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = "#${model.palletBarcode?:""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.mdp))
                    .background(Primary.copy(0.2f))
                    .padding(vertical = 4.mdp, horizontal = 10.mdp)
            ) {
                MyText(
                    text = model.total?.toString() ?: "0",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.W500,
                    color = Primary
                )
            }
        }
    }
}


@Preview
@Composable
private fun CheckingDetailPreview() {
    LoadingDetailContent()

}