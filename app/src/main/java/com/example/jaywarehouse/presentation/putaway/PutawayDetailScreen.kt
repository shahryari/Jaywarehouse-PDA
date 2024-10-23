package com.example.jaywarehouse.presentation.putaway

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.putaway.model.PutawaysRow
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
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
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.packing.contracts.PackingDetailContract
import com.example.jaywarehouse.presentation.picking.contracts.PickingDetailContract
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayDetailContract
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PutawayDetailScreen(
    navigator: DestinationsNavigator,
    putRow: ReadyToPutRow,
    fillLocation: Boolean,
    viewModel: PutawayDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(putRow,fillLocation)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
//    val localFocusManager = LocalFocusManager.current
//    val locationFocusRequester = remember {
//        FocusRequester()
//    }
//    val barcodeFocusRequester = remember {
//        FocusRequester()
//    }

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PutawayDetailContract.Effect.NavBack -> navigator.popBackStack()
                PutawayDetailContract.Effect.NavToDashboard -> {
                    navigator.navigate(DashboardScreenDestination){
                        popUpTo(PutawayScreenDestination){
                            inclusive = true
                        }
                    }
                }

                PutawayDetailContract.Effect.MoveFocus -> {
//                    barcodeFocusRequester.requestFocus()
                }
            }
        }
    }
    PutawayDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PutawayDetailContent(
    state: PutawayDetailContract.State = PutawayDetailContract.State(),
    onEvent: (PutawayDetailContract.Event)->Unit = {}
) {
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PutawayDetailContract.Event.OnRefresh)
        }
    )
    val locationFocusRequester = remember {
        FocusRequester()
    }
    val barcodeFocusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(PutawayDetailContract.Event.HideToast)
        }
    }
    MyScaffold(
        loadingState = state.loadingState
    ) {

        Box(Modifier.fillMaxSize()) {

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.mdp))
                            .background(Color.Black.copy(0.85f))
                            .clickable {
                                onEvent(PutawayDetailContract.Event.OnNavBack)
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
                        stringResource(id = R.string.putaway),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.size(15.mdp))
                if(state.putRow!=null)
                    PutawayItem(
                        model = state.putRow,
                        enableShowDetail = true,
                        showAll = state.showHeaderDetail,
                        onClick = {
                            onEvent(PutawayDetailContract.Event.OnShowHeaderDetail(!state.showHeaderDetail))
                        }
                    )
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.boxNumber,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeBoxNumber(it))
                    },
                    label = "Box Number ...",
                    focusRequester = FocusRequester(),
                    onAny = {
                        onEvent(PutawayDetailContract.Event.CheckBoxNumber)
                        locationFocusRequester.requestFocus()
                    },
                    enabled = (state.enableLocation && state.enableBoxNumber) || state.putaways.isEmpty(),
                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.enableBoxNumber && state.boxNumber.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(PutawayDetailContract.Event.OnChangeBoxNumber(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            MyIcon(icon = R.drawable.vuesax_bulk_box) {
                                onEvent(PutawayDetailContract.Event.CheckBoxNumber)
                                locationFocusRequester.requestFocus()
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.location,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeLocation(it))
                    },
                    label = "Location ...",
                    focusRequester = locationFocusRequester,
                    onAny = {
                        onEvent(PutawayDetailContract.Event.CheckLocation)
                        if (!state.enableLocation) barcodeFocusRequester.requestFocus()
                    },
                    enabled = state.enableLocation,
                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            if (state.enableLocation && state.location.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(PutawayDetailContract.Event.OnChangeLocation(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning && state.enableLocation) {
                                RefreshIcon(isRefreshing = true)
                            } else {
                                MyIcon(icon = R.drawable.location) {
                                    onEvent(PutawayDetailContract.Event.CheckLocation)
                                    if (!state.enableLocation) barcodeFocusRequester.requestFocus()
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyInput(
                    value = state.barcode,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeBarcode(it))
                    },
                    label = "Barcode ...",
                    enabled = !state.enableLocation,
                    onAny = {
                        onEvent(PutawayDetailContract.Event.ScanBarcode)
                    },
                    focusRequester = barcodeFocusRequester,
                    readOnly = state.loadingState != Loading.NONE,
                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            if (!state.enableLocation && state.barcode.text.isNotEmpty())MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                                onEvent(PutawayDetailContract.Event.OnChangeBarcode(TextFieldValue()))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                            if (state.isScanning && !state.enableLocation){
                                RefreshIcon(isRefreshing = true)
                            }
                            else {
                                MyIcon(icon = R.drawable.barcode) {
                                    onEvent(PutawayDetailContract.Event.ScanBarcode)
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(15.mdp))
                if (state.putaways.isNotEmpty())Column(
                    Modifier
                        .shadow(2.mdp, RoundedCornerShape(10.mdp))
                        .fillMaxWidth()
                        .pullRefresh(refreshState)
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
                    LazyColumn(
                        Modifier.heightIn(min = 100.mdp)
                    ) {
                        itemsIndexed(state.putaways.reversed()){ i, it->
                            RegisteredItem(
                                state.putaways.size-i,it.date,it.time
                            ) {
                                onEvent(PutawayDetailContract.Event.OnSelectPut(it.putawayScanID))
                            }
                            Spacer(modifier = Modifier.size(7.mdp))
                        }
                        item {
                            onEvent(PutawayDetailContract.Event.OnReachEnd)
                        }
                    }
                    Spacer(modifier = Modifier.size(80.mdp))
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
    if (state.selectedPutaway!=null){
        ConfirmDialog(onDismiss = {
            onEvent(PutawayDetailContract.Event.OnSelectPut(null))
        }) {
            onEvent(PutawayDetailContract.Event.OnRemovePut(state.selectedPutaway))
        }
    }
    if (state.error.isNotEmpty()) {
        ErrorDialog(
            onDismiss = {
                onEvent(PutawayDetailContract.Event.CloseError)
            },
            message = state.error
        )
    }
    if (state.showFinishAlertDialog){
        MyAlertDialog(
            onDismiss = {
                onEvent(PutawayDetailContract.Event.HideFinishDialog)
            }
        )
    }
    LaunchedEffect(key1 = state.enableLocation) {
        if (state.enableLocation){
            locationFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            barcodeFocusRequester.requestFocus()
        }
    }
}

@Composable
fun MyAlertDialog(
    onDismiss: ()->Unit,
    message: String = "Your put operation is completed successfully",
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = "Ok",
        onPositiveClick = {
            onDismiss()
        },
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
    }
}


@Composable
fun RegisteredItem(i: Int, date: String , time: String,onRemove:()->Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Gray2)
            .padding(top = 4.mdp, bottom = 4.mdp, start = 2.mdp, end = 15.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

        MyText(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
        MyText(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
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
                modifier = Modifier.size(12.mdp)
            )
        }
    }
}

@Preview
@Composable
private fun PutawayDetailPreview() {
    PutawayDetailContent()

}