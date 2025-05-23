package com.example.jaywarehouse.presentation.cycle_count

import android.graphics.drawable.Icon
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.MainListItem
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.cycle_count.contracts.CycleCountContract
import com.example.jaywarehouse.presentation.cycle_count.viewmodels.CycleViewModel
import com.example.jaywarehouse.presentation.destinations.CountingDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.CycleDetailScreenDestination
import com.example.jaywarehouse.presentation.destinations.LoadingDetailScreenDestination
import com.example.jaywarehouse.presentation.loading.contracts.LoadingContract
import com.example.jaywarehouse.presentation.loading.viewmodels.LoadingViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun CycleScreen(
    navigator: DestinationsNavigator,
    viewModel: CycleViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is CycleCountContract.Effect.NavToCycleCountDetail -> {
                    navigator.navigate(CycleDetailScreenDestination(it.item))
                }

                CycleCountContract.Effect.NavBack -> {
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
    state: CycleCountContract.State = CycleCountContract.State(),
    onEvent: (CycleCountContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }

    val listState = rememberLazyListState()

    var lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }


    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CycleCountContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CycleCountContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(CycleCountContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column {

                Column(
                    Modifier
                        .weight(1f)
                        .padding(15.mdp)
                ) {
                    TopBar(
                        title = "Cycle Count",
                        subTitle = "Choose a Location",
                        onBack = {
                            onEvent(CycleCountContract.Event.OnBackPressed)
                        }
                    )
                    Spacer(modifier = Modifier.size(10.mdp))
                    SearchInput(
                        onSearch = {
                            onEvent(CycleCountContract.Event.OnSearch(it.text))
                        },
                        value = state.keyword,
                        isLoading = state.loadingState == Loading.SEARCHING,
                        onSortClick = {
                            onEvent(CycleCountContract.Event.OnShowSortList(true))
                        },
                        hideKeyboard = state.lockKeyboard,
                        focusRequester = searchFocusRequester
                    )
                    Spacer(modifier = Modifier.size(15.mdp))
                    MyLazyColumn(
                        modifier = Modifier.weight(1f),
                        items = state.cycleList,
                        state = listState,
                        itemContent = {_,it->
                            CycleItem(it) {
                                onEvent(CycleCountContract.Event.OnNavToCycleCountDetail(it))
                            }
                        },
                        onReachEnd = {
                            onEvent(CycleCountContract.Event.OnReachedEnd)
                        }
                    )
                }
                Row(
                    Modifier
                        .shadow(1.mdp)
                        .fillMaxWidth()
                        .background(
                            Gray3
                        )
                        .padding(12.mdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    MyText(
                        text = "${lastItem.value.coerceAtMost(state.cycleCount)} ",
                        color = Primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    MyText(
                        text = " of ",
                        color = Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    MyText(
                        text = "${state.cycleList.size}",
                        color = Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    MyText(
                        text = " from ",
                        color = Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                    MyText(
                        text = "${state.cycleCount}",
                        color = Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CycleCountContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CycleCountContract.Event.OnChangeSort(it))
            }
        )
    }
}


@Composable
fun CycleItem(
    model: CycleRow,
    showCount: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .then(if(onClick!=null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            Modifier
                .padding(6.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailCard(
                "Location",
                detail = model.locationCode,
                icon = null,
                modifier= Modifier.weight(2.4f)
            )
            Column(Modifier.weight(1f)) {
                Row(Modifier.align(Alignment.Start),verticalAlignment = Alignment.CenterVertically) {
                    if (model.isEmpty)
                        Icon(
                            painterResource(R.drawable.direct_normal),
                            contentDescription = "",
                            Modifier.size(24.mdp),
                            tint = Black
                        )
                    else
                        Icon(
                            painterResource(R.drawable.direct),
                            contentDescription = "",
                            Modifier.size(24.mdp),
                            tint = Primary
                        )
                    Spacer(Modifier.size(8.mdp))
                    if (model.counting == 1)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.mdp))
                                .background(Primary.copy(0.2f))
                                .padding(vertical = 4.mdp, horizontal = 10.mdp)
                        ) {
                            MyText(
                                text = "Counting",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        }
                }
                if (showCount)Spacer(Modifier.size(5.mdp))
                if(showCount)MyText(
                    "Items : ${model.detailCount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CheckingPreview() {
    CheckingContent()
}