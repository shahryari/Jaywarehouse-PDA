package com.example.jaywarehouse.presentation.packing

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.packing.model.PackingDetailRow
import com.example.jaywarehouse.data.packing.model.PackingRow
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyInput
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.RefreshIcon
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.presentation.packing.contracts.PackingDetailContract
import com.example.jaywarehouse.presentation.packing.viewmodels.PackingDetailViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PackingDetailScreen(
    navigator: DestinationsNavigator,
    packingRow: PackingRow,
    viewModel: PackingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(packingRow)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PackingDetailContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    PackingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackingDetailContent(
    state: PackingDetailContract.State,
    onEvent: (PackingDetailContract.Event)->Unit
) {
    val barcodeFocusRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PackingDetailContract.Event.OnRefresh)
        }
    )
    LaunchedEffect(key1 = Unit) {
        barcodeFocusRequester.requestFocus()
    }
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(PackingDetailContract.Event.HideToast)
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
                                onEvent(PackingDetailContract.Event.OnNavBack)
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
                        stringResource(id = R.string.packing),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                if (state.packingRow!=null){
                    PackedItem(
                        packingRow = state.packingRow,
                        onClick = { /*TODO*/ },
                        onRemove = { /*TODO*/ },
                        enableShowDetail = true,
                        showDeleteIcon = false
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.barcode,
                    onValueChange = {
                        onEvent(PackingDetailContract.Event.OnBarcodeChange(it))
                    },
                    label = "Barcode ...",
                    focusRequester = barcodeFocusRequester,
                    onAny = {
                        onEvent(PackingDetailContract.Event.ScanBarcode)
                    },
                    hideKeyboard = state.lockKeyboard,
                    readOnly = state.loadingState != Loading.NONE && state.error.isNotEmpty(),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.barcode.text.isNotEmpty()){
                                MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                    onEvent(PackingDetailContract.Event.OnBarcodeChange(TextFieldValue()))
                                }
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning){
                                RefreshIcon(isRefreshing = true)
                            }
                            else {
                                MyIcon(icon = R.drawable.barcode) {
                                    onEvent(PackingDetailContract.Event.ScanBarcode)
                                }
                            }

                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                if(state.packingDetails.isNotEmpty())Column(
                    Modifier
                        .shadow(2.mdp, RoundedCornerShape(10.mdp))
                        .clip(RoundedCornerShape(10.mdp))
                        .background(Color.White)
                        .padding(12.mdp)
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        MyText(
                            "Product",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        MyText(
                            "Quantity",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.size(15.mdp))
                    LazyColumn(modifier = Modifier.heightIn(max = (60*state.packingDetails.size).mdp)) {
                        itemsIndexed(state.packingDetails.reversed()){ i, it->
                            PackingDetailItem(i = state.packingDetails.size-i, row = it) {
                                onEvent(PackingDetailContract.Event.SelectedItem(it.packingDetailID))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        item {
                            onEvent(PackingDetailContract.Event.OnReachEnd)
                        }
                    }
                }
            }
            SuccessToast(
                Modifier
                    .padding(12.mdp)
                    .align(alignment = Alignment.TopCenter),message = state.toast)
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(alignment = Alignment.TopCenter) )
            Row(
                Modifier
                    .padding(15.mdp)
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        onEvent(PackingDetailContract.Event.OnShowSubmitAndNew(true))
                    },
                    modifier = Modifier
                        .weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Black)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier
                            .border(2.mdp, Color.White, RoundedCornerShape(7.mdp))
                            .padding(4.mdp)){
                            Icon(
                                Icons.Default.Check,
                                contentDescription ="",
                                tint = Color.White,
                                modifier = Modifier.size(16.mdp)
                            )
                        }
                        Spacer(modifier = Modifier.size(5.mdp))
                        MyText(
                            text = "Submit & New",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.size(5.mdp))
                Button(
                    onClick = {
                        onEvent(PackingDetailContract.Event.OnShowSubmit(true))
                    },
                    modifier = Modifier
                        .weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier
                            .border(2.mdp, Black, CircleShape)
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
                            text = "Submit",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                    }
                }
            }
        }
    }
    if (state.selectedItem!=null){
        ConfirmDialog(onDismiss = {
            onEvent(PackingDetailContract.Event.SelectedItem(null))
        }) {
            onEvent(PackingDetailContract.Event.OnRemove(state.selectedItem))
        }
    }
    if (state.showSubmitDialog || state.showSubmitAndNewDialog){
        ConfirmDialog(onDismiss = {
            onEvent(PackingDetailContract.Event.OnShowSubmit(false))
            onEvent(PackingDetailContract.Event.OnShowSubmitAndNew(false))
        }, message = "Are you sure you want to submit this packing?") {
            if (state.showSubmitAndNewDialog)
                onEvent(PackingDetailContract.Event.OnSubmitAndNew)
            else
                onEvent(PackingDetailContract.Event.OnSubmit)
        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(
            onDismiss = {
                onEvent(PackingDetailContract.Event.CloseError)
            },
            message = state.error
        )
    }
}

@Composable
fun PackingDetailItem(
    i: Int,
    row: PackingDetailRow,
    onRemove: ()->Unit
) {
    var showName by remember {
        mutableStateOf(false)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Gray2)
            .clickable {
                showName = !showName
            }
            .padding(top = 4.mdp, bottom = 4.mdp, start = 2.mdp, end = 15.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                MyText(
                    text = row.barcode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                AnimatedVisibility(visible = showName) {
                    Column {
                        Spacer(modifier = Modifier.size(10.mdp))
                        MyText(
                            text = row.model,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
        Row(
            Modifier
                .clip(CircleShape)
                .clickable {
                    onRemove()
                }
                .border(1.mdp, Color.Gray, CircleShape)
                .padding(horizontal = 4.mdp, vertical = 4.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyText(
                text = row.quantity.toString(),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.mdp),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.size(15.mdp))
            Box(modifier = Modifier
                .clip(CircleShape)
                .background(Red)
                .padding(horizontal = 17.mdp, vertical = 10.mdp)){
                Spacer(modifier = Modifier
                    .width(10.mdp)
                    .height(2.mdp)
                    .background(Color.White))
            }
        }
    }
}

@Preview
@Composable
private fun PackingDetailPreview() {
    MyScaffold {
        PackingDetailContent(PackingDetailContract.State(),{})
//        PackingDetailItem(i = 1, row = PackingDetailRow("test","test",1,1,1)) {
//
//        }
    }
}