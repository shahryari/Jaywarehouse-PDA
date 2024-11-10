package com.example.jaywarehouse.presentation.shipping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.shipping.models.ShippingDetailRow
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyInput
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.RefreshIcon
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingDetailContract
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingDetailViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun ShippingDetailScreen(
    navigator: DestinationsNavigator,
    shippingRow: ShippingRow,
    viewModel: ShippingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(shippingRow)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                ShippingDetailContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    ShippingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShippingDetailContent(
    state: ShippingDetailContract.State = ShippingDetailContract.State(),
    onEvent: (ShippingDetailContract.Event) -> Unit = {},
) {
    val barcodeFocusRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(ShippingDetailContract.Event.OnRefresh)
        }
    )
    LaunchedEffect(key1 = Unit) {
        barcodeFocusRequester.requestFocus()
    }
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(ShippingDetailContract.Event.HideToast)
        }
    }
    MyScaffold(loadingState = state.loadingState) {
        Box(
            Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)) {

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(15.mdp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.mdp))
                            .background(Color.Black.copy(0.85f))
                            .clickable {
                                onEvent(ShippingDetailContract.Event.OnNavBack)
                            }
                            .padding(5.mdp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(26.mdp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    MyText(
                        stringResource(id = R.string.shipping),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(ShippingDetailContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(ShippingDetailContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    showSortIcon = false,
                    onSortClick = {
                        onEvent(ShippingDetailContract.Event.OnShowFilterList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                )
                Spacer(modifier = Modifier.size(15.mdp))
                if (state.shippingRow!=null){
                    ShippingItem(
                        row = state.shippingRow,
                        onClick = {
                            onEvent(ShippingDetailContract.Event.OnShowAll)
                        },
                        onRemove = {},
                        enableShowDetail = true,
                        showAll = state.showAll,
                        enableRemove = false
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.barcode,
                    onValueChange = {
                        onEvent(ShippingDetailContract.Event.OnBarcodeChange(it))
                    },
                    label = "Barcode ...",
                    focusRequester = barcodeFocusRequester,
                    onAny = {
                        onEvent(ShippingDetailContract.Event.ScanBarcode)
                    },
                    readOnly = state.loadingState != Loading.NONE,
                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.barcode.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(ShippingDetailContract.Event.OnBarcodeChange(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning){
                                RefreshIcon(isRefreshing = true)
                            }
                            else {
                                MyIcon(icon = R.drawable.barcode) {
                                    onEvent(ShippingDetailContract.Event.ScanBarcode)
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                if (state.shippingDetailList.isNotEmpty())Column(
                    Modifier
                        .shadow(2.mdp, RoundedCornerShape(10.mdp))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.mdp))
                        .background(Color.White)
                        .padding(12.mdp)
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        MyText(
                            "Packing Number",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.size(15.mdp))
                    LazyColumn(Modifier.heightIn(max = (60*state.shippingDetailList.size).mdp)) {
                        itemsIndexed(state.shippingDetailList){ i, it->
                            ShippingDetailItem(i = state.shippingDetailList.size-i, row = it) {
                                onEvent(ShippingDetailContract.Event.OnSelectShippingDetail(it.packingID))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        item {
                            onEvent(ShippingDetailContract.Event.OnReachEnd)
                        }
                    }
                }
            }
            SuccessToast(
                Modifier
                    .padding(12.mdp)
                    .align(alignment = Alignment.TopCenter),message = state.toast)
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

            Button(
                onClick = {
                    onEvent(ShippingDetailContract.Event.OnShowInvoiceConfirm(true))
                },
                modifier = Modifier
                    .padding(15.mdp)
                    .fillMaxWidth(0.8f)
                    .align(alignment = Alignment.BottomCenter),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .border(2.mdp, Black, RoundedCornerShape(7.mdp))
                        .padding(4.mdp)){
                        Icon(
                            Icons.Default.Check,
                            contentDescription ="",
                            tint = Black,
                            modifier = Modifier.size(16.mdp)
                        )
                    }
                    Spacer(modifier = Modifier.size(15.mdp))
                    MyText(
                        text = "Create Invoice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }
        }
    }
    if (state.selectedShippingDetail!=null){
        ConfirmDialog(onDismiss = {
            onEvent(ShippingDetailContract.Event.OnSelectShippingDetail(null))
        }) {
            onEvent(ShippingDetailContract.Event.OnRemoveShippingDetail(state.selectedShippingDetail))
        }
    }
    if (state.showInvoiceConfirm){
        ConfirmDialog(onDismiss = {
            onEvent(ShippingDetailContract.Event.OnShowInvoiceConfirm(false))
        }, message = "Are you sure to create invoice?") {
            onEvent(ShippingDetailContract.Event.OnInvoice)
        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(
            onDismiss = {
                onEvent(ShippingDetailContract.Event.OnClearError)
            },
            message = state.error
        )
    }
}

@Composable
fun ShippingDetailItem(i: Int,row: ShippingDetailRow,onRemove: ()->Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Gray2)
            .padding(top = 4.mdp, bottom = 4.mdp, start = 2.mdp, end = 15.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Box(modifier = Modifier
                .size(40.mdp)
                .clip(CircleShape)
                .background(Black),
                contentAlignment = Alignment.Center
            ){
                MyText(
                    i.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            Column {
                Row(Modifier.fillMaxWidth(0.6f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    MyText(
                        text = row.packingNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    MyText(
                        text = "Items: ${row.itemCount?:0}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Row(Modifier.fillMaxWidth(0.6f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    MyText(
                        text = row.customerName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )

                    MyText(
                        text = "Sum: ${row.sumPackedQty?:0}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(20.mdp))
        Box(
            Modifier
                .clip(RoundedCornerShape(4.mdp))
                .background(Color.Gray)
                .clickable {
                    onRemove()
                }
                .padding(5.mdp)
        ) {

            Icon(
                Icons.Default.Clear,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(15.mdp)
            )
        }
    }
}

@Preview
@Composable
private fun ShippingDetailPreview() {
    MyScaffold {
        ShippingDetailContent()
    }
}