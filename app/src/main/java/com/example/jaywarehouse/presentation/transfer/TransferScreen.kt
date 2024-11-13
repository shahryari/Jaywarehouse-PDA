package com.example.jaywarehouse.presentation.transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.transfer.models.LocationTransferRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.packing.DialogInput
import com.example.jaywarehouse.presentation.transfer.contracts.TransferContract
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferPickViewModel
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferPutViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun TransferPutScreen(
    viewModel: TransferPutViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    TransferContent(state = state, onEvent = onEvent)
}

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun TransferPickScreen(
    viewModel: TransferPickViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    TransferContent(state = state, onEvent = onEvent)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TransferContent(
    state: TransferContract.State,
    onEvent: (TransferContract.Event)->Unit
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(TransferContract.Event.OnRefresh)
        }
    )

    val sortList = mapOf("Created On" to "CreatedOn","Receiving" to "Receiving","Progress" to "Progress")

    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(TransferContract.Event.HideToast)
        }
    }

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()

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
                    text = "Transfer ${if (state.isPick) "Pick" else "Put"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(TransferContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(TransferContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.REFRESHING,
                    onSortClick = {
                        onEvent(TransferContract.Event.ShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.transferList){
                        TransferItem(row = it) {

                        }
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(TransferContract.Event.OnReachToEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(80.mdp))
                    }
                }
            }
//            if (state.loadingState == Loading.LOADING)CircularProgressIndicator(Modifier.align(Alignment.Center))
            SuccessToast(message = state.toast)
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

            Column(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 85.mdp, end = 15.mdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = state.showBoxButton) {
                    Box(
                        modifier = Modifier
                            .shadow(6.mdp, CircleShape)
                            .clip(CircleShape)
                            .background(Orange)
                            .clickable {
                                onEvent(TransferContract.Event.OnShowTransferBox(true))
                            }
                            .padding(15.mdp)
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.vuesax_linear_box),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(30.mdp)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(10.mdp))
                Box(
                    modifier = Modifier
                        .shadow(6.mdp, CircleShape)
                        .clip(CircleShape)
                        .background(Orange)
                        .combinedClickable(
                            enabled = true,
                            onLongClick = {
                                onEvent(TransferContract.Event.ShowTransferButton(!state.showBoxButton))
                            },
                            onClick = {
                                onEvent(TransferContract.Event.OnShowTransferItem(true))
                            }
                        )
                        .padding(15.mdp)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.vuesax_bulk_direct_inbox),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(30.mdp)
                    )
                }

            }
        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(onDismiss = {
            onEvent(TransferContract.Event.CloseError)
        }, message = state.error)
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {},
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(TransferContract.Event.OnSortChange(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(TransferContract.Event.OnOrderChange(it))
            }
        )
    }
    if (state.isPick){
        if (state.showTransferItem){
            PickTransferItemDialog(state = state,onEvent)
        }
        if (state.showTransferBox){
            PickTransferBoxDialog(state = state,onEvent)
        }
    } else {
        if (state.showTransferItem) {
            PutTransferItemDialog(state = state, onEvent)
        }
        if (state.showTransferBox){
            PutTransferBoxDialog(state = state, onEvent)
        }
    }

}

@Composable
fun TransferItem(row: LocationTransferRow,onClick: () -> Unit) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Model",row.model, R.drawable.vuesax_outline_3d_cube_scan,MaterialTheme.typography.titleSmall),
        item2 = BaseListItemModel("Barcode",row.barcode,R.drawable.barcode,MaterialTheme.typography.titleSmall),
        item3 = BaseListItemModel("Created On",row.date?:"",R.drawable.vuesax_linear_calendar_2,MaterialTheme.typography.bodySmall),
        quantity = row.locationTransferNumber?:"",
        quantityTitle = "",
        quantityIcon = R.drawable.note,
        scan = row.locationCode?:"",
        enableShowDetail = true,
        scanTitle = "",
        scanIcon = R.drawable.location
    )
}

@Composable
fun PickTransferItemDialog(
    state: TransferContract.State,
    onEvent: (TransferContract.Event) -> Unit
) {
    val locationRequester = FocusRequester()
    val barcodeRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        locationRequester.requestFocus()
    }
    BasicDialog(
        onDismiss = {
            onEvent(TransferContract.Event.OnShowTransferItem(false))
        },
        positiveButton = "Submit",
        onPositiveClick = {
            onEvent(TransferContract.Event.OnTransferItem)
        },
        title = "Pick item for transfer",
        isLoading = state.isTransferring,
        showCloseIcon = true
    ) {
        Column {
            Spacer(modifier = Modifier.size(10.mdp))
            MyText(
                text = "Source Location" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.locationCode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnLocationCodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        barcodeRequester.requestFocus()
                    }
                },
                onAny = {
                    barcodeRequester.requestFocus()
                },
                focusRequester = locationRequester,
                hideKeyboard = state.lockKeyboard,
                icon = R.drawable.location,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
            MyText(
                text = "Barcode" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.barcode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnBarcodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        barcodeRequester.freeFocus()
                    }
                },
                onAny = {
                    barcodeRequester.freeFocus()
                },
                icon = R.drawable.barcode,
                hideKeyboard = state.lockKeyboard,
                focusRequester = barcodeRequester,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
        }
    }
}

@Composable
fun PickTransferBoxDialog(
    state: TransferContract.State,
    onEvent: (TransferContract.Event) -> Unit
) {
    val locationRequester = remember { FocusRequester() }
    val barcodeRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = Unit) {
        locationRequester.requestFocus()
    }

    BasicDialog(
        onDismiss = {
            onEvent(TransferContract.Event.OnShowTransferBox(false))
        },
        positiveButton = "Submit",
        onPositiveClick = {
            onEvent(TransferContract.Event.OnTransferBox)
        },
        title = "Pick box for transfer",
        isLoading = state.isTransferring,
        showCloseIcon = true
    ) {
        Column {
            Spacer(modifier = Modifier.size(10.mdp))
            MyText(
                text = "Source Location" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.locationCode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnLocationCodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        barcodeRequester.requestFocus()
                    }
                },
                onAny = {
                    barcodeRequester.requestFocus()
                },
                focusRequester = locationRequester,
                icon = R.drawable.location,
                hideKeyboard = state.lockKeyboard,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
            MyText(
                text = "Box Number" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.boxNumber,
                onValueChange ={
                    onEvent(TransferContract.Event.OnBoxNumberChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        barcodeRequester.freeFocus()
                    }
                },
                onAny = {
                    barcodeRequester.freeFocus()
                },
                focusRequester = barcodeRequester,
                hideKeyboard = state.lockKeyboard,
                icon = R.drawable.vuesax_linear_box,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
        }
    }
}

@Composable
fun PutTransferItemDialog(
    state: TransferContract.State,
    onEvent: (TransferContract.Event) -> Unit
) {
    val locationRequester = remember {
        FocusRequester()
    }
    val barcodeRequester = remember {
        FocusRequester()
    }
    val boxNumberRequest = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        locationRequester.requestFocus()
    }
    BasicDialog(
        onDismiss = {
            onEvent(TransferContract.Event.OnShowTransferItem(false))
        },
        positiveButton = "Submit",
        onPositiveClick = {
            onEvent(TransferContract.Event.OnTransferItem)
        },
        title = "Put transfer item",
        isLoading = state.isTransferring,
        showCloseIcon = true
    ) {
        Column {
            Spacer(modifier = Modifier.size(10.mdp))
            MyText(
                text = "Destination Location" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.locationCode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnLocationCodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        barcodeRequester.requestFocus()
                    }
                },
                onAny = {
                    barcodeRequester.requestFocus()
                },
                focusRequester = locationRequester,
                icon = R.drawable.location,
                hideKeyboard = state.lockKeyboard,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
            MyText(
                text = "Barcode" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.barcode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnBarcodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        boxNumberRequest.requestFocus()
                    }
                },
                onAny = {
                    boxNumberRequest.requestFocus()
                },
                hideKeyboard = state.lockKeyboard,
                focusRequester = barcodeRequester,
                icon = R.drawable.barcode,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
            MyText(
                text = "Box Number" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.boxNumber,
                onValueChange ={
                    onEvent(TransferContract.Event.OnBoxNumberChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        boxNumberRequest.freeFocus()
                    }
                },
                onAny = {
                    boxNumberRequest.freeFocus()
                },
                focusRequester = boxNumberRequest,
                hideKeyboard = state.lockKeyboard,
                icon = R.drawable.vuesax_linear_box,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
        }
    }
}

@Composable
fun PutTransferBoxDialog(
    state: TransferContract.State,
    onEvent: (TransferContract.Event) -> Unit
) {
    val locationRequester = remember {
        FocusRequester()
    }
    val boxNumberRequest = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = Unit) {
        locationRequester.requestFocus()
    }
    BasicDialog(
        onDismiss = {
            onEvent(TransferContract.Event.OnShowTransferBox(false))
        },
        positiveButton = "Submit",
        onPositiveClick = {
            onEvent(TransferContract.Event.OnTransferBox)
        },
        title = "Put transfer box",
        isLoading = state.isTransferring,
        showCloseIcon = true
    ) {
        Column {
            Spacer(modifier = Modifier.size(10.mdp))
            MyText(
                text = "Destination Location" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.locationCode,
                onValueChange ={
                    onEvent(TransferContract.Event.OnLocationCodeChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        boxNumberRequest.requestFocus()
                    }
                },
                onAny = {
                    boxNumberRequest.requestFocus()
                },
                focusRequester = locationRequester,
                hideKeyboard = state.lockKeyboard,
                keyboardType = KeyboardType.Text,
                icon = R.drawable.location
            )
            Spacer(modifier = Modifier.size(20.mdp))
            MyText(
                text = "Box Number" ,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start),
                color = Color.White
            )
            Spacer(modifier = Modifier.size(5.mdp))
            DialogInput(
                state.boxNumber,
                onValueChange ={
                    onEvent(TransferContract.Event.OnBoxNumberChange(it))
                    if (it.text.endsWith('\n') || it.text.endsWith('\r')){
                        boxNumberRequest.freeFocus()
                    }
                },
                onAny = {
                    boxNumberRequest.freeFocus()
                },
                focusRequester = boxNumberRequest,
                icon = R.drawable.vuesax_linear_box,
                hideKeyboard = state.lockKeyboard,
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.size(20.mdp))
        }
    }
}
@Composable
fun SelectableInput(
    value: String,
    onClick: ()->Unit,
    label: String = ""
) {
    Row(
        Modifier
            .shadow(2.mdp, RoundedCornerShape(10.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 9.mdp, horizontal = 10.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (value.isEmpty()) {
            MyText(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        MyText(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = Black
        )
    }
}



@Preview
@Composable
private fun TransferPreview() {
    MyScaffold {
        TransferContent(TransferContract.State(),{})
        PutTransferItemDialog(state = TransferContract.State()) {

        }
    }
}