package com.linari.presentation.counting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.receiving.model.ReceivingRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.BasicDialog
import com.linari.presentation.common.composables.MyIcon
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
import com.linari.presentation.counting.contracts.CountingDetailContract
import com.linari.presentation.counting.viewmodels.CountingDetailViewModel
import com.linari.presentation.destinations.CountingInceptionScreenDestination
import com.linari.ui.theme.Black
import com.linari.ui.theme.Border
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray4
import com.linari.ui.theme.Green
import com.linari.ui.theme.Orange
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

    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

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
                    subTitle = stringResource(id = R.string.counting),
                    titleTag = state.warehouse?.name?:"",
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
                    state = listState,

                    itemContent = {_,it->
                        CountingDetailItem(
                            it,
                            onClick = {
                                onEvent(CountingDetailContract.Event.OnDetailClick(it))
                            },
                            confirmable = it.countQuantity == null,
                            onConfirm = {
                                onEvent(CountingDetailContract.Event.OnSelectDetail(it))
                            }
                        )
                    },
                    spacerSize = 7.mdp,
                    endSpace = 100.mdp,
                    onReachEnd = {
                        onEvent(CountingDetailContract.Event.OnReachedEnd)
                    }
                )
            }
            if (state.countingDetailRow.isNotEmpty()) Column(Modifier.align(Alignment.BottomCenter)) {
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
                            "${stringResource(id = R.string.total)}: "+state.total.removeZeroDecimal().toString(),
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
                            "${stringResource(id = R.string.count)}: "+state.scan.removeZeroDecimal().toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W500
                        )

                    }
                }
                HorizontalDivider(thickness = 1.mdp,color = Border)
                RowCountView(
                    current = lastItem.value,
                    group = state.countingDetailRow.size,
                    total = state.rowCount
                )
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
    if (state.selectedDetail != null){
        ConfirmDialog(
            onDismiss = {
                onEvent(CountingDetailContract.Event.OnSelectDetail(null))
            },
            message = stringResource(id=R.string.counting_complete_confirm),
            isLoading = state.isCompleting
        ) {
            onEvent(CountingDetailContract.Event.OnConfirm(state.selectedDetail))
        }
    }

}

@Composable
fun ConfirmDialog(
    onDismiss: ()->Unit,
    title: String = "",
    message: String = stringResource(R.string.remove_confirm),
    tint: Color = Orange,
    isLoading: Boolean = false,
    onConfirm: ()->Unit
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = stringResource(id = R.string.yes),
        positiveButtonTint = tint,
        negativeButton = stringResource(id = R.string.cancel),
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
            fontSize = 17.sp,
            color = Color.Black,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Start
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
    confirmable: Boolean = true,
    onConfirm: () -> Unit = {}
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel(stringResource(id = R.string.product_name),model.productName,R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (showDetail) BaseListItemModel(stringResource(id = R.string.product_code),model.productCode,R.drawable.keyboard2) else null,
        item3 = if (showDetail) BaseListItemModel(stringResource(id = R.string.barcode),model.productBarcodeNumber?:"",R.drawable.barcode) else null,
        item4 = if (showDetail) BaseListItemModel(stringResource(id = R.string.product_type),model.quiddityTypeTitle?:"",R.drawable.vuesax_linear_box) else null,
        scan = (model.countQuantity?.removeZeroDecimal()?.toString() ?: "") + if (model.isWeight == true && model.countQuantity != null) " kg" else "",
        scanTitle = stringResource(id = R.string.count),
        scanContent = {
//           if (confirmable) MyIcon(
//                icon = R.drawable.tick,
//                onClick = {
//                    onConfirm()
//                }
//            )
        },
        quantity = model.quantity.removeZeroDecimal().toString() + if (model.isWeight == true) " kg" else "",
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