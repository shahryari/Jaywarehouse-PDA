package com.linari.presentation.counting

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
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.receiving.model.ReceivingRow
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
import com.linari.presentation.counting.contracts.CountingContract
import com.linari.presentation.counting.viewmodels.CountingViewModel
import com.linari.presentation.destinations.CountingDetailScreenDestination
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Destination(style = ScreenTransition::class)
@Composable
fun CountingScreen(
    navigator: DestinationsNavigator,
    isCrossDock: Boolean = false,
    viewModel: CountingViewModel = koinViewModel(
        parameters = {
            parametersOf(isCrossDock)
        }
    )
) {
    val state= viewModel.state
    val onEvent = viewModel::setEvent


    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is CountingContract.Effect.NavToReceivingDetail ->{
                    navigator.navigate(CountingDetailScreenDestination(it.receivingRow,isCrossDock))
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
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            try{
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            }catch (e: Exception) {
                0
            }
        }
    }


    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(CountingContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CountingContract.Event.ClearError)
        },
        onRefresh = {
            onEvent(CountingContract.Event.OnRefresh)
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
                        stringResource(R.string.counting),
                        titleTag = state.warehouse?.name ?: "",
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
                        state = listState,
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
                RowCountView(
                    current = lastItem.value,
                    group = state.countingList.size,
                    total = state.rowCount
                )
            }

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
                if (receivingRow.receivingTypeID == 2L){
                    DetailCard(
                        stringResource(R.string.customer),
                        icon = R.drawable.user_square,
                        detail = receivingRow.customerFullName?:""
                    )
                } else {
                    DetailCard(
                        stringResource(R.string.supplier),
                        icon = R.drawable.user_square,
                        detail = receivingRow.supplierFullName?:""
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        stringResource(R.string.warehouse_name),
                        icon = R.drawable.building,
                        detail = receivingRow.warehouseName?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        stringResource(R.string.receiving_date),
                        icon = R.drawable.vuesax_linear_calendar_2,
                        detail =
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.parse(receivingRow.receivingDate?:"", DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(
                                    DateTimeFormatter.ISO_LOCAL_DATE)
                            } else {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val date: Date = inputFormat.parse(receivingRow.receivingDate!!)!!

                                val outputFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
                                outputFormat.format(date)

                            }
                        } catch (e: Exception){
                            receivingRow.receivingDate?:""
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
                    text = "${stringResource(R.string.total)}: "+receivingRow.total.removeZeroDecimal().toString(),
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
                    text = "${stringResource(R.string.count)}: " + (receivingRow.count?.removeZeroDecimal()?.toString()?:""),
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
private fun CountingPreview() {
    CountingContent(
        state = CountingContract.State(
            countingList = listOf(
                ),
        )
    )
}