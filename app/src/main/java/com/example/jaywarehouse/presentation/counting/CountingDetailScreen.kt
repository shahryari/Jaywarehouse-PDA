package com.example.jaywarehouse.presentation.counting

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.example.jaywarehouse.presentation.counting.viewmodels.CountingDetailViewModel
import com.example.jaywarehouse.presentation.destinations.CountingInceptionScreenDestination
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Border
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray4
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun CountingDetailScreen(
    navigator: DestinationsNavigator,
    receivingRow: ReceivingRow,
    isCrossDock: Boolean = false,
    viewModel: CountingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(receivingRow,isCrossDock)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                CountingDetailContract.Effect.NavBack -> navigator.popBackStack()
                is CountingDetailContract.Effect.OnNavToInception -> {
                    navigator.navigate(CountingInceptionScreenDestination(it.detail,receivingRow.receivingID,isCrossDock))
                }
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
    val focusRequester = FocusRequester()

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
        onEvent(CountingDetailContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        toast = state.toast,
        onHideToast = {
            onEvent(CountingDetailContract.Event.HideToast)

        },
        error = state.error,
        onRefresh = {
            onEvent(CountingDetailContract.Event.OnRefresh)
        },
        onCloseError = {
            onEvent(CountingDetailContract.Event.CloseError)
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
                    title = state.countingRow?.referenceNumber?:"",
                    subTitle = "Counting",
                    onBack = {
                        onEvent(CountingDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(CountingDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(CountingDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.countingDetailRow,
                    itemContent = {_,it->
                        CountingDetailItem(it) {
                            onEvent(CountingDetailContract.Event.OnDetailClick(it))
                        }
                    },
                    spacerSize = 7.mdp,
                    onReachEnd = {

                    }
                )
            }
            if (state.countingDetailRow.isNotEmpty())Column(Modifier.align(Alignment.BottomCenter)) {
                HorizontalDivider(thickness = 1.mdp,color = Border)
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        Modifier
                            .weight(1f)
                            .background(Gray3)
                            .padding(10.mdp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.vuesax_outline_box_tick),
                            contentDescription = "",
                            modifier = Modifier.size(30.mdp),
                            tint = Black
                        )
                        Spacer(modifier = Modifier.size(10.mdp))
                        MyText(
                            "Total: "+state.total.toString(),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W500,
                        )
                    }
                    Row(
                        Modifier
                            .weight(1f)
                            .background(Gray4)
                            .padding(10.mdp)
                           ,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.scanner),
                            contentDescription = "",
                            modifier = Modifier.size(30.mdp),
                            tint = Black
                        )
                        Spacer(modifier = Modifier.size(10.mdp))
                        MyText(
                            "Scan: "+state.scan.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W500
                        )

                    }
                }
            }
        }
    }

    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CountingDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CountingDetailContract.Event.OnSelectSort(it))
            },
        )
    }

}

@Composable
fun ConfirmDialog(
    onDismiss: ()->Unit,
    title: String = "",
    message: String = "Are you sure to remove this item?",
    tint: Color = Orange,
    isLoading: Boolean = false,
    onConfirm: ()->Unit
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = "Yes",
        positiveButtonTint = tint,
        negativeButton = "Cancel",
        isLoading = isLoading,
        onPositiveClick = {
            onConfirm()
        },
        onNegativeClick = {
            onDismiss()
        }
    ) {
        if (message.isNotEmpty())MyText(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.W400
        )
//        if (description.isNotEmpty() && message.isNotEmpty())Spacer(modifier = Modifier.size(5.mdp))
//        if (description.isNotEmpty())MyText(
//            text = description,
//            style = MaterialTheme.typography.bodyLarge,
//            color = DarkGray,
//            fontWeight = FontWeight.W400
//        )
        Spacer(Modifier.size(7.mdp))
    }   
}


@Composable
fun CountingDetailItem(
    model: ReceivingDetailRow,
    showDetail: Boolean = true,
    onClick: () -> Unit,
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName,R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (showDetail) BaseListItemModel("Product Code",model.productCode,R.drawable.fluent_barcode_scanner_20_regular) else null,
        item3 = if (showDetail) BaseListItemModel("Barcode",model.productBarcodeNumber,R.drawable.note) else null,
        item4 = if (showDetail) BaseListItemModel("Product Type",model.quiddityTypeTitle?:"",R.drawable.vuesax_linear_box) else null,
        scan = model.countQuantity?:0.0,
        scanTitle = "Count",
        quantity = model.quantity
    )
}




@Preview
@Composable
private fun CountingDetailPreview() {
    CountingDetailContent(
//        state = CountingDetailContract.State(
//            loadingState = Loading.NONE,
//            countingRow = ReceivingRow(receivingDate = "today", supplierFullName = "test", countedQuantity = 50, receivingDetailSumQuantity = 20, receivingDetailCount = 13, referenceNumber = "353523525", receivingID = 0, receivingTypeID = 3, receivingTypeTitle = "test",total =0 , count = 2),
//            countingDetailRow = listOf(
//            )
//        )
    )
    ConfirmDialog({}) { }
}