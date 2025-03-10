package com.example.jaywarehouse.presentation.counting

import android.os.Build
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
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
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
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingViewModel
import com.example.jaywarehouse.presentation.destinations.CountingDetailScreenDestination
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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

                CountingContract.Effect.NavBack -> {
                    navigator.popBackStack()
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

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CountingContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CountingContract.Event.ClearError)
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
                    stringResource(R.string.counting),
                    onBack = {
                        onEvent(CountingContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(CountingContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(CountingContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.countingList,
                    itemContent = {_,it->
                        CountListItem(it) {
                            onEvent(CountingContract.Event.OnNavToReceivingDetail(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(CountingContract.Event.OnListEndReached)
                    }
                )
            }

            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CountingContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CountingContract.Event.OnSelectSort(it))
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

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Primary.copy(0.2f))
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = receivingRow.receivingTypeTitle?:"",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                    MyText(
                        text = "#${receivingRow.referenceNumber?:""}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )

                }
                Spacer(modifier = Modifier.size(10.mdp))
                if (receivingRow.description != null){
                    DetailCard(
                        "Description",
                        icon = R.drawable.hashtag,
                        detail = receivingRow.description
                    )
                    Spacer(Modifier.size(10.mdp))
                }
                DetailCard(
                    "Supplier",
                    icon = R.drawable.barcode,
                    detail = receivingRow.supplierFullName?:""
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        "Warehouse Name",
                        icon = R.drawable.building,
                        detail = receivingRow.warehouseName?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        "Receiving Date",
                        icon = R.drawable.vuesax_linear_calendar_2,
                        detail = try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(receivingRow.receivingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(
                                    DateTimeFormatter.ISO_LOCAL_DATE)
                            } else {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val date: Date = inputFormat.parse(receivingRow.receivingDate)!!

                                val outputFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
                                outputFormat.format(date)

                            }
                        } catch (e: Exception){
                            receivingRow.receivingDate
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
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
                    text = "Total: "+receivingRow.total.toString(),
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
                    text = "Scan: " + (receivingRow.count?.toString()?:"0"),
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
private fun CountingPreview() {
    CountingContent(
        state = CountingContract.State(
            countingList = listOf(
                ReceivingRow(receivingDate = "today", supplierFullName = "test", referenceNumber = "353523525", receivingID = 0, receivingTypeTitle = "general", receivingTypeID = 3, total = 0, count = 0, description = "test", warehouseName = "test"),
                ),
        )
    )
}