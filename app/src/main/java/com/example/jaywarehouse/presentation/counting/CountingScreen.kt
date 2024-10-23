package com.example.jaywarehouse.presentation.counting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.ProgressIndicator
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingViewModel
import com.example.jaywarehouse.presentation.destinations.CountingDetailScreenDestination
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Green
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Red
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun CountingScreen(
    navigator: DestinationsNavigator,
    viewModel: CountingViewModel = koinViewModel()
) {
    val state= viewModel.state
    val onEvent = viewModel::setEvent


    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is CountingContract.Effect.NavToReceivingDetail ->{
                    navigator.navigate(CountingDetailScreenDestination(it.receivingRow))
                }

            }
        }
        
    }
    CountingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
private fun CountingContent(
    state: CountingContract.State = CountingContract.State(),
    onEvent: (CountingContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }


    val refreshState = rememberPullRefreshState(
        refreshing =  state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(CountingContract.Event.OnRefresh)
        }
    )
    val sortList = mapOf("Created On" to "CreatedOn","Receiving" to "Receiving","Progress" to "Progress")

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CountingContract.Event.FetchData)
    }
    MyScaffold(
        offset = (-70).mdp,
        loadingState = state.loadingState
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {
                MyText(
                    text = stringResource(id = R.string.counting),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(CountingContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(CountingContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(CountingContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier
                    .fillMaxSize()
                ) {
                    items(state.countingList){
                        CountListItem(it){
                            onEvent(CountingContract.Event.OnNavToReceivingDetail(it))
                        }
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(CountingContract.Event.OnListEndReached)
                    }
                    item { Spacer(modifier = Modifier.size(70.mdp)) }
                }
                Spacer(modifier = Modifier.size(70.mdp))
            }

            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(onDismiss = {
            onEvent(CountingContract.Event.ClearError)
        }, message = state.error)
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CountingContract.Event.OnShowSortList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CountingContract.Event.OnSelectSort(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(CountingContract.Event.OnSelectOrder(it))
            }
        )
    }
}

@Composable
fun CountListItem(
    receivingRow: ReceivingRow,
    shrink: Boolean = false,
    onClick: ()->Unit
) {
    val color = when(receivingRow.progress){
        in 0..99->{
            Orange
        }
        100 -> Green
        else -> Red
    }
    var visibleDetails by remember {
        mutableStateOf(true)
    }
    Column(
        Modifier
            .shadow(2.mdp, RoundedCornerShape(10.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .background(Color.White)
            .clickable {
                if (shrink) visibleDetails = !visibleDetails
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

                    MyText(
                        text = "#${receivingRow.receivingNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.mdp))
                            .background(Orange)
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = receivingRow.receivingTypeTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontFamily = poppins,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.size(10.mdp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.vuesax_linear_calendar_2),
                        contentDescription = null,
                        modifier = Modifier.size(24.mdp),
                        tint = Black
                    )
                    Spacer(modifier = Modifier.size(3.mdp))
                    MyText(
                        text = receivingRow.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (receivingRow.description?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.size(15.mdp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.vuesax_bold_quote_up),
                            contentDescription = "",
                            tint = Black.copy(0.7f),
                            modifier = Modifier.size(25.mdp)
                        )
                        Spacer(modifier = Modifier.size(7.mdp))
                        MyText(
                            text = receivingRow.description,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.mdp)
                                .clip(RoundedCornerShape(5.mdp))
                                .background(color.copy(0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(receivingRow.progress.toFloat() / 100f)
                                    .height(6.mdp)
                                    .clip(RoundedCornerShape(5.mdp))
                                    .background(color)
                            )
                        }
                        Spacer(modifier = Modifier
                            .size(17.mdp)
                            .clip(CircleShape)
                            .background(color))
                    }
                    Spacer(modifier = Modifier.size(4.mdp))
                    MyText(
                        text = "${receivingRow.progress}%",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        style = MaterialTheme.typography.labelSmall,
                        color = color.copy(0.7f)
                    )
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .background(Black)
                    .padding(vertical = 7.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.vuesax_outline_box_tick),
                    contentDescription = "",
                    modifier = Modifier.size(28.mdp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(7.mdp))
                MyText(
                    text = "Total: "+receivingRow.sumQuantity.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .background(Orange)
                    .padding(vertical = 7.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.scanner),
                    contentDescription = "",
                    modifier = Modifier.size(28.mdp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(7.mdp))
                MyText(
                    text = "Scan: " + receivingRow.receivingDetailSumQuantityScanCount.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FootText(
    text: String,
    modifier: Modifier = Modifier
) {
    MyText(
        text = text,
        modifier = modifier
            .background(Black)
            .padding(horizontal = 10.mdp, vertical = 15.mdp)
        ,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
private fun CountingPreview() {
    CountingContent()
}