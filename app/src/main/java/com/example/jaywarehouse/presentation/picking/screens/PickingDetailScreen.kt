package com.example.jaywarehouse.presentation.picking.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.data.picking.models.ReadyToPickRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
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
import com.example.jaywarehouse.presentation.destinations.PickingCustomerScreenDestination
import com.example.jaywarehouse.presentation.destinations.PickingListScreenDestination
import com.example.jaywarehouse.presentation.picking.contracts.PickingDetailContract
import com.example.jaywarehouse.presentation.picking.viewmodels.PickingDetailViewModel
import com.example.jaywarehouse.presentation.putaway.MyAlertDialog
import com.example.jaywarehouse.presentation.putaway.RegisteredItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PickingDetailScreen(
    navigator: DestinationsNavigator,
    pickingRow: ReadyToPickRow,
    customer: CustomerToPickRow,
    fillLocation: Boolean = false,
    viewModel: PickingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(pickingRow, customer,fillLocation)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PickingDetailContract.Effect.NavigateBack -> navigator.popBackStack()
                PickingDetailContract.Effect.NavigateToParent -> navigator.navigate(PickingCustomerScreenDestination){
                    popUpTo(PickingListScreenDestination){
                        inclusive = true
                    }
                }
            }
        }
    }
    PickingDetailContent(state,onEvent)
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PickingDetailContent(
//    locationFocusRequester: FocusRequester,
//    barcodeFocusRequester: FocusRequester,
    state: PickingDetailContract.State,
    onEvent: (PickingDetailContract.Event) -> Unit
) {
    val refreshState = rememberPullRefreshState(refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PickingDetailContract.Event.OnRefresh)
        }
    )
    val locationFocusRequester = remember {
        FocusRequester()
    }
    val barcodeFocusRequester = remember {
        FocusRequester()
    }
    val focusManger = LocalFocusManager.current

//    LaunchedEffect(key1 = state.enableLocation) {
//        if (state.enableLocation){
//            locationFocusRequester.requestFocus()
//        } else {
//            barcodeFocusRequester.requestFocus()
//        }
//    }
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(PickingDetailContract.Event.HideToast)
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
                                onEvent(PickingDetailContract.Event.OnNavBack)
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
                        stringResource(id = R.string.picking),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                if(state.pickingRow!=null){
                    BaseListItem(
                        onClick = {
                            onEvent(PickingDetailContract.Event.OnShowDetailChange(!state.showDetail))
                        },
                        item1 = BaseListItemModel("Model",state.pickingRow.model,R.drawable.vuesax_outline_3d_cube_scan),
                        item2 = if (state.showDetail)BaseListItemModel("Customer",state.customer?.customerName?:"",R.drawable.vuesax_linear_user) else null,
                        item4 = BaseListItemModel("Item Code",state.pickingRow.barcode,R.drawable.fluent_barcode_scanner_20_regular),
                        item5 = BaseListItemModel("Location",state.pickingRow.locationCode,R.drawable.location),
                        item6 = if (state.showDetail)BaseListItemModel("Brand",state.pickingRow.brand?:"",R.drawable.note) else null,
                        item7 = if (state.showDetail)BaseListItemModel("Product Type",state.pickingRow.type?:"",R.drawable.note) else null,
                        item8 = if (state.showDetail)BaseListItemModel("Size",state.pickingRow.size?:"",R.drawable.hashtag) else null,
                        item9 = if (state.showDetail)BaseListItemModel("Gender",state.pickingRow.gender ?:"",R.drawable.vuesax_linear_calendar_add) else null,
                        quantity = state.pickingRow.quantity,
                        enableShowDetail = true,
                        scan = state.pickingRow.scanCount?:0
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.location,
                    onValueChange = {
                        onEvent(PickingDetailContract.Event.OnLocationChanged(it))
                    },
                    label = "Location ...",
                    focusRequester =locationFocusRequester,
                    onAny = {
                        onEvent(PickingDetailContract.Event.OnCheckLocation)
                    },
                    hideKeyboard = state.lockKeyboard,
                    enabled = state.enableLocation,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.enableLocation && state.location.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(PickingDetailContract.Event.OnLocationChanged(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning && state.enableLocation) {
                                RefreshIcon(isRefreshing = true)
                            } else {
                                MyIcon(icon = R.drawable.location) {
                                    onEvent(PickingDetailContract.Event.OnCheckLocation)
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.barcode,
                    onValueChange = {
                        onEvent(PickingDetailContract.Event.OnBarcodeChanged(it))
                    },
                    label = "Barcode ...",
                    enabled = !state.enableLocation,
                    readOnly = state.loadingState != Loading.NONE,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    onAny = {
                        onEvent(PickingDetailContract.Event.OnScan)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!state.enableLocation && state.barcode.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(PickingDetailContract.Event.OnBarcodeChanged(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning && !state.enableLocation){
                                RefreshIcon(isRefreshing = true)
                            }
                            else {
                                MyIcon(icon = R.drawable.barcode) {
                                    onEvent(PickingDetailContract.Event.OnScan)
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                if (state.pickings.isNotEmpty()) Column(
                    Modifier
                        .shadow(2.mdp, RoundedCornerShape(10.mdp))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.mdp))
                        .background(Color.White)
                        .padding(12.mdp)
                ) {
                    MyText(
                        "Registered On",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(15.mdp))
                    LazyColumn(modifier = Modifier.heightIn(max = (60*state.pickings.size).mdp)) {
                        itemsIndexed(state.pickings.reversed()){ i, it->
                            RegisteredItem(
                                state.pickings.size-i,it.date,it.time
                            ) {
                                onEvent(PickingDetailContract.Event.OnSelectPickedItem(it.pickingScanID))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        item {
                            onEvent(PickingDetailContract.Event.OnReachToEnd)
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

        }
    }
    if (state.selectedItem!=null){
        ConfirmDialog(onDismiss = {
            onEvent(PickingDetailContract.Event.OnSelectPickedItem(null))
        }) {
            onEvent(PickingDetailContract.Event.OnRemovePickedItem(state.selectedItem))
        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(
            onDismiss = {
                onEvent(PickingDetailContract.Event.ClearError)
            },
            message = state.error
        )
    }
    if (state.showFinishDialog){
        MyAlertDialog(
            onDismiss = {
                onEvent(PickingDetailContract.Event.HideFinish)
            },
            message = "You scanned all of items"
        )
    }

    LaunchedEffect( key1 = state.enableLocation) {
        if (state.enableLocation) {
            locationFocusRequester.requestFocus()
        } else {
            focusManger.clearFocus()
            barcodeFocusRequester.requestFocus()
        }
    }

}

@Preview
@Composable
private fun PickingDetailPreview() {
    MyScaffold {
        PickingDetailContent(state = PickingDetailContract.State(),{})
    }
}