package com.example.jaywarehouse.presentation.checking

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.presentation.checking.contracts.CheckingContract
import com.example.jaywarehouse.presentation.checking.viewModels.CheckingViewModel
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
import com.example.jaywarehouse.presentation.destinations.CheckingDetailScreenDestination
import com.example.jaywarehouse.ui.theme.Primary
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = "Checking",
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
                    modifier= Modifier
                    .fillMaxSize(),
                    items = state.checkingList,
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
    enableShowDetail: Boolean = false,
    onClick: () -> Unit
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

                    if(model.b2BCustomer!=null)Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Primary.copy(0.2f))
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = model.b2BCustomer,
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
                    icon = R.drawable.user_square,
                    detail = model.customerName?:""
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