package com.linari.presentation.rs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.rs.models.WaybillInfoRow
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.MainListItem
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
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.picking.PickingItem
import com.linari.presentation.picking.contracts.PickingContract
import com.linari.presentation.rs.contracts.WaybillContract
import com.linari.presentation.rs.viewmodels.WaybillViewModel
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun WaybillScreen(
    navigator: DestinationsNavigator,
    viewModel: WaybillViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                WaybillContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    WaybillContent(state, onEvent)
}


@Composable
fun WaybillContent(
    state: WaybillContract.State = WaybillContract.State(),
    onEvent: (WaybillContract.Event) -> Unit = {}
) {

    val searchRequester = remember {
        FocusRequester()
    }

    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(Unit) {
        searchRequester.requestFocus()
        onEvent(WaybillContract.Event.FetchData)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(WaybillContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(WaybillContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(WaybillContract.Event.OnRefresh)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    stringResource(R.string.waybill),
                    onBack = {
                        onEvent(WaybillContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(WaybillContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(WaybillContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier=  Modifier.fillMaxSize(),
                    items = state.waybillList,
                    itemContent = {_,it->
                        WaybillItem(it) {
                            onEvent(WaybillContract.Event.OnSelectWaybill(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(WaybillContract.Event.OnReachEnd)
                    },
                    state = listState
                )
            }
            RowCountView(
                Modifier.align(alignment = Alignment.BottomCenter),
                current = lastItem.value,
                group = state.waybillList.size,
                total = state.rowCount
            )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(WaybillContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(WaybillContract.Event.ChangeSort(it))
            }
        )
    }
    if (state.selectedWaybill != null) {
        ConfirmDialog(
            onDismiss = {
                onEvent(WaybillContract.Event.OnSelectWaybill(null))
            },
            message = "Are you sure to Integrate this waybill?",
            isLoading = state.isIntegrating
        ) {
            onEvent(WaybillContract.Event.OnIntegrateWaybill(state.selectedWaybill))
        }
    }
}

@Composable
fun WaybillItem(
    model: WaybillInfoRow,
    onClick: () -> Unit
) {
    Column(
        Modifier.shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(8.mdp)
    ) {
        if(model.referenceNumber!=null)Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.mdp))
                .background(Primary.copy(0.2f))
                .padding(vertical = 4.mdp, horizontal = 10.mdp)
        ) {
            MyText(
                text = model.referenceNumber,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
        } else {
            Spacer(Modifier.size(10.mdp))
        }
        if (model.waybillNumber!=null) {
            MyText(
                text = "#${model.waybillNumber}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        } else {
            Spacer(Modifier.size(10.mdp))
        }
        Spacer(Modifier.size(10.mdp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailCard(
                title = stringResource(R.string.customer),
                detail = model.driverFullName?:"",
                icon = R.drawable.user_square,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(15.mdp))
            DetailCard(
                title = stringResource(R.string.customer_code),
                detail = model.driverTin?:"",
                icon = R.drawable.note,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.size(10.mdp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailCard(
                title = "Driver",
                detail = model.driverFullName?:"",
                icon = R.drawable.user_square,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(15.mdp))
            DetailCard(
                title = "Driver Tin",
                detail = model.driverTin?:"",
                icon = R.drawable.note,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.size(10.mdp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailCard(
                title = "Car No.",
                detail = model.carNumber?:"",
                icon = R.drawable.barcode,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(15.mdp))
            DetailCard(
                title = "Trailer No.",
                detail = model.trailerNumber?:"",
                icon = R.drawable.vuesax_linear_box,
                modifier = Modifier.weight(1f)
            )
        }
    }
}