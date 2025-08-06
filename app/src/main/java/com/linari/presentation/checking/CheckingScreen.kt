package com.linari.presentation.checking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.linari.data.common.utils.mdp
import com.linari.R
import com.linari.data.checking.models.CheckingListGroupedRow
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.presentation.checking.contracts.CheckingContract
import com.linari.presentation.checking.viewModels.CheckingViewModel
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.MainListItem
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.RowCountView
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.destinations.CheckingDetailScreenDestination
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun CheckingScreen(
    navigator: DestinationsNavigator,
    viewModel: CheckingViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is CheckingContract.Effect.NavToCheckingDetail -> {
                    navigator.navigate(CheckingDetailScreenDestination(it.item))
                }

                CheckingContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    CheckingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckingContent(
    state: CheckingContract.State = CheckingContract.State(),
    onEvent: (CheckingContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }

    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }



    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CheckingContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CheckingContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(CheckingContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(15.mdp)
                ) {
                    TopBar(
                        title = stringResource(R.string.checking),
                        onBack = {
                            onEvent(CheckingContract.Event.OnBackPressed)
                        }
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    SearchInput(
                        onSearch = {
                            onEvent(CheckingContract.Event.OnSearch(it.text))
                        },
                        value = state.keyword,
                        isLoading = state.loadingState == Loading.SEARCHING,
                        onSortClick = {
                            onEvent(CheckingContract.Event.OnShowSortList(true))
                        },
                        hideKeyboard = state.lockKeyboard,
                        focusRequester = searchFocusRequester
                    )
                    Spacer(modifier = Modifier.size(15.mdp))
                    MyLazyColumn(
                        modifier= Modifier.weight(1f)
                            .fillMaxSize(),
                        items = state.checkingList,
                        state = listState,
                        itemContent = {_,it->
                            CheckingItem(it) {
                                onEvent(CheckingContract.Event.OnNavToCheckingDetail(it))
                            }
                        },
                        onReachEnd = {
                            onEvent(CheckingContract.Event.OnReachedEnd)

                        }
                    )
                }
                RowCountView(
                    current = lastItem.value,
                    group = state.checkingList.size,
                    total = state.rowCount
                )
            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CheckingContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CheckingContract.Event.OnChangeSort(it))
            }
        )
    }
}


@Composable
fun CheckingItem(
    model: CheckingListGroupedRow,
    onClick: () -> Unit
) {
    MainListItem(
        onClick = onClick,
        typeTitle = model.customerTypeTitle,
        modelNumber = model.customerCode,
        item1 = BaseListItemModel(
            stringResource(R.string.customer),
            model.customerName,
            R.drawable.user_square
        ),
        total = model.count.removeZeroDecimal(),
        totalTitle = stringResource(R.string.total),
        count = model.sumQuantity?.removeZeroDecimal()?:"",
        countTitle = stringResource(R.string.qty),
    )
}

@Preview
@Composable
private fun CheckingPreview() {
    CheckingContent()
}