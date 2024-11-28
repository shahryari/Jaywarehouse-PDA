package com.example.jaywarehouse.presentation.putaway

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.PutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayContract
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayViewModel
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

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
                    navigator.navigate(PutawayDetailScreenDestination(it.readyToPutRow))
                }

                PutawayContract.Effect.NavBack -> {
                    navigator.popBackStack()
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
    val searchFocusRequester = remember {
        FocusRequester()
    }


    val refreshState = rememberPullRefreshState(
        refreshing =  state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PutawayContract.Event.OnRefresh)
        }
    )

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PutawayContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PutawayContract.Event.ClearError)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {
                TopBar(
                    stringResource(R.string.putaway),
                    onBack = {
                        onEvent(PutawayContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
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
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.puts,
                    itemContent = {_,it->
                        PutawayItem(it) {
                            onEvent(PutawayContract.Event.OnNavToPutawayDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PutawayContract.Event.OnReachedEnd)
                    }
                )

            }

            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PutawayContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PutawayContract.Event.OnChangeSort(it))
            }
        )
    }
}


@Composable
fun PutawayItem(
    model: PutawayListGroupedRow,
    enableShowDetail: Boolean = false,
    showAll: Boolean = true,
    onClick:()->Unit
) {
    var visibleDetails by remember {
        mutableStateOf(true)
    }
    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable {
                if (enableShowDetail) visibleDetails = !visibleDetails
                onClick()
            }
    ) {
        AnimatedVisibility(visible = visibleDetails) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(15.mdp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Primary.copy(0.2f))
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = model.receivingTypeTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                    MyText(
                        text = "#${model.referenceNumber?:""}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )

                }
                Spacer(modifier = Modifier.size(10.mdp))
                DetailCard(
                    "Supplier",
                    icon = R.drawable.barcode,
                    detail = model.supplierFullName?:""
                )
                Spacer(modifier = Modifier.size(15.mdp))

            }
        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .background(Primary)
                    .padding(vertical = 7.mdp, horizontal = 10.mdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vuesax_outline_box_tick),
                    contentDescription = "",
                    modifier = Modifier.size(28.mdp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(7.mdp))
                MyText(
                    text = "Total: "+model.total,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .background(Primary.copy(0.2f))
                    .padding(vertical = 7.mdp, horizontal = 10.mdp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.scanner),
                    contentDescription = "",
                    modifier = Modifier.size(28.mdp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.size(7.mdp))
                MyText(
                    text = "Scan: " + model.count,
                    color = Primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
private fun PoutawayPreview() {
    PutawayContent()
}