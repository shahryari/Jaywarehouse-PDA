package com.linari.presentation.manual_putaway

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.data.common.utils.mdp
import com.linari.R
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.putaway.model.PutawayListGroupedRow
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
import com.linari.presentation.destinations.ManualPutawayScreenDestination
import com.linari.presentation.destinations.PutawayDetailScreenDestination
import com.linari.presentation.destinations.PutawayDetailScreenDestination.invoke
import com.linari.presentation.manual_putaway.contracts.PutawayContract
import com.linari.presentation.manual_putaway.viewmodels.PutawayViewModel
import com.linari.ui.theme.Primary
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
                    navigator.navigate(ManualPutawayScreenDestination(it.readyToPutRow))
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
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }
    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PutawayContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PutawayContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(PutawayContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    stringResource(R.string.putaway),
                    titleTag = state.warehouse?.name ?: "",
                    onBack = {
                        onEvent(PutawayContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PutawayContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
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
                    },
                    state = listState
                )

            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.puts.size,
                total = state.rowCount
            )

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

                    if (!model.receivingTypeTitle.isNullOrBlank())Box(
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
                    icon = R.drawable.user_square,
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
                    text = "Total: "+model.total.removeZeroDecimal(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
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
                    text = "Scan: " + (model.count?.removeZeroDecimal()?:""),
                    color = Primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
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