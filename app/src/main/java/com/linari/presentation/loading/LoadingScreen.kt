package com.linari.presentation.loading

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.linari.data.common.utils.mdp
import com.linari.R
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.presentation.common.composables.DetailCard
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
import com.linari.presentation.destinations.LoadingDetailScreenDestination
import com.linari.presentation.loading.contracts.LoadingContract
import com.linari.presentation.loading.viewmodels.LoadingViewModel
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun LoadingScreen(
    navigator: DestinationsNavigator,
    viewModel: LoadingViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is LoadingContract.Effect.NavToLoadingDetail -> {
                    navigator.navigate(LoadingDetailScreenDestination(it.item))
                }

                LoadingContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    LoadingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    state: LoadingContract.State = LoadingContract.State(),
    onEvent: (LoadingContract.Event)->Unit = {}
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
        onEvent(LoadingContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(LoadingContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(LoadingContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = "Loading" ,
                    titleTag = state.warehouse?.name?:"",
                    onBack = {
                        onEvent(LoadingContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(LoadingContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(LoadingContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.loadingList,
                    itemContent = {_,it->
                        LoadingItem(it) {
                            onEvent(LoadingContract.Event.OnNavToLoadingDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(LoadingContract.Event.OnReachedEnd)
                    },
                    state = listState,
                )
            }
            RowCountView(
                modifier = Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.loadingList.size,
                total = state.rowCount
            )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(LoadingContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(LoadingContract.Event.OnChangeSort(it))
            }
        )
    }
}


@Composable
fun LoadingItem(
    model: LoadingListGroupedRow,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable {
                onClick()
            }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(15.mdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                if(model.customerTypeTitle!=null)Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = model.customerTypeTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                } else  {
                    Spacer(Modifier.size(10.mdp))
                }
                MyText(
                    text = "#${model.customerCode?:""}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )

            }
            Spacer(modifier = Modifier.size(10.mdp))
            DetailCard(
                "Customer",
                icon = R.drawable.vuesax_linear_user_tag,
                detail = model.customerName?:""
            )
        }
    }
}

@Preview
@Composable
private fun CheckingPreview() {
    LoadingContent()
}