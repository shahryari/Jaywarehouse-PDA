package com.linari.presentation.loading

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.data.pallet.model.PalletConfirmRow
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
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.loading.contracts.LoadingDetailContract
import com.linari.presentation.loading.viewmodels.LoadingDetailViewModel
import com.linari.ui.theme.Green
import com.linari.ui.theme.Orange
import com.linari.ui.theme.Primary
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
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }
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
        },
        onRefresh = {
            onEvent(LoadingDetailContract.Event.OnRefresh)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.loadingRow?.customerName?.trim()?:"",
                    subTitle = "Loading",
                    titleTag = state.warehouse?.name?:"",
                    onBack = {
                        onEvent(LoadingDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(

                    onSearch = {
                        onEvent(LoadingDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
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
                    state = listState,
                    spacerSize = 7.mdp
                )
            }
            RowCountView(
                modifier = Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.details.size,
                total = state.rowCount
            )
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
            title = "Confirm Loading",
            isLoading = state.onSaving,
            message = "Are you sure to confirm this loading?",
            tint = Orange
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
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    true
                }
            }
        }
    )
    LaunchedEffect(swipeState.currentValue) {
        swipeState.reset()
    }
    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Row(
                Modifier.fillMaxSize()
                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
                    .clip(RoundedCornerShape(6.mdp))
                    .background(Primary)
                    .padding(vertical = 6.mdp, horizontal = 8.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "",
                    tint = Color.White
                )
                Spacer(Modifier.size(10.mdp))
                MyText(
                    text = "Confirm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
            }
        }
    ) {
        Row(
            Modifier.fillMaxWidth()
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
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
//            Box(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(4.mdp))
//                    .background(Primary.copy(0.2f))
//                    .padding(vertical = 4.mdp, horizontal = 10.mdp)
//            ) {
//                MyText(
//                    text = model.total?.toString() ?: "0",
//                    style = MaterialTheme.typography.labelSmall,
//                    fontWeight = FontWeight.W500,
//                    color = Primary
//                )
//            }
        }
    }
}


@Preview
@Composable
private fun CheckingDetailPreview() {
    LoadingDetailContent()

}