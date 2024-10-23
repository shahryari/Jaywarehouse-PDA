package com.example.jaywarehouse.presentation.counting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyInput
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.ReceivingItem
import com.example.jaywarehouse.presentation.common.composables.RefreshIcon
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingDetailViewModel
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun CountingDetailScreen(
    navigator: DestinationsNavigator,
    receivingRow: ReceivingRow,
    viewModel: CountingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(receivingRow)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                CountingDetailContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    CountingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun CountingDetailContent(
    state: CountingDetailContract.State = CountingDetailContract.State(),
    onEvent: (CountingDetailContract.Event)->Unit = {}
) {
    val barcodeFocusRequester = FocusRequester()

    val sortList = mapOf("Created On" to "CreatedOn","Product Name" to "ProductName","Product Code" to "ProductCode","Reference Number" to "ReferenceNumber")
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(CountingDetailContract.Event.OnRefresh) }
    )
    LaunchedEffect(key1 = Unit) {
        barcodeFocusRequester.requestFocus()
    }

    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(CountingDetailContract.Event.HideToast)
        }
    }
    MyScaffold(
        loadingState = state.loadingState
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
            Column(
                Modifier
                    .pullRefresh(refreshState)
                    .fillMaxSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(12.mdp))
                        .background(Color.Black.copy(0.85f))
                        .clickable {
                            onEvent(CountingDetailContract.Event.OnNavBack)
                        }
                        .padding(5.mdp)){
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(26.mdp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    MyText(
                        stringResource(id = R.string.counting),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(CountingDetailContract.Event.OnChangeKeyword(it))
                    },
                    onSearch = {
                        onEvent(CountingDetailContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(CountingDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                )
                Spacer(modifier = Modifier.size(10.mdp))
                MyInput(
                    value = state.barcode,
                    onValueChange = {
                        onEvent(CountingDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {
                        onEvent(CountingDetailContract.Event.ScanBarcode)
                    },
                    focusRequester = barcodeFocusRequester,
                    label = "Scan Barcode",
                    readOnly = state.loadingState != Loading.NONE,
                    hideKeyboard = state.lockKeyboard,
                    trailingIcon ={
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.barcode.text.isNotEmpty()) MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(CountingDetailContract.Event.OnClearBarcode)
                            }
                            Spacer(modifier = Modifier.size(7.mdp))

                            if (state.isScanLoading){
                                RefreshIcon(isRefreshing = true)
                            } else {
                                MyIcon(icon = R.drawable.barcode) {
                                    onEvent(CountingDetailContract.Event.ScanBarcode)
                                }
                            }

                        }
                    }
                )

                Spacer(modifier = Modifier.size(20.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    stickyHeader {
                        if (state.countingRow != null)CountListItem(state.countingRow, shrink = true){}
                        Spacer(modifier = Modifier.size(15.mdp))
                    }
                    items(state.countingDetailRow){
                        ReceivingItem(it){
                            onEvent(CountingDetailContract.Event.OnSelectDetail(it.barcode))
                        }
                        Spacer(modifier = Modifier.size(7.mdp))
                    }
                    item {
                        onEvent(CountingDetailContract.Event.OnReachedEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(70.mdp))
                    }
                }

            }
            SuccessToast(message = state.toast)
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

        }
    }
    if (state.error.isNotEmpty()){
        ErrorDialog(
            onDismiss = {
                onEvent(CountingDetailContract.Event.CloseError)
            },
            state.error
        )

    }
    if (state.selectedDetail!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(CountingDetailContract.Event.OnSelectDetail(null))
            },
            onConfirm = {
                onEvent(CountingDetailContract.Event.RemoveScanBarcode(state.selectedDetail))
            }
        )
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CountingDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CountingDetailContract.Event.OnSelectSort(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(CountingDetailContract.Event.OnSelectOrder(it))
            }
        )
    }
    if (state.showConfirm){
        ConfirmDialog(
            onDismiss = {
                onEvent(CountingDetailContract.Event.OnShowConfirm(false))
            },
            message = "You scanned all item, Are you sure to scan more?",
            description = ""
        ) {
            onEvent(CountingDetailContract.Event.ConfirmScanBarcode)
        }
    }

}

@Composable
fun ConfirmDialog(
    onDismiss: ()->Unit,
    message: String = "Are you sure to remove this item?",
    description: String = "You are not able to retrieve this item.",
    onConfirm: ()->Unit
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = "Yes",
        negativeButton = "Cancel",
        onPositiveClick = {
            onConfirm()
        },
        onNegativeClick = {
            onDismiss()
        }
    ) {
        Icon(
            painterResource(id = R.drawable.broken___essentional__ui___danger_triang),
            contentDescription = "",
            tint = Orange,
            modifier = Modifier.size(80.mdp)
        )
        Spacer(Modifier.size(10.mdp))
        MyText(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(7.mdp))
        MyText(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(0.6f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
    }
}





@Preview
@Composable
private fun CountingDetailPreview() {
    CountingDetailContent(state = CountingDetailContract.State(loadingState = Loading.NONE))
}