package com.linari.presentation.putaway

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.data.putaway.model.PutawayListRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TitleView
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.destinations.DashboardScreenDestination
import com.linari.presentation.destinations.PutawayScreenDestination
import com.linari.presentation.putaway.contracts.PutawayDetailContract
import com.linari.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun PutawayDetailScreen(
    navigator: DestinationsNavigator,
    putRow: PutawayListGroupedRow,
    viewModel: PutawayDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(putRow)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent
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
    val focusRequester = FocusRequester()

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PutawayDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(PutawayDetailContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(PutawayDetailContract.Event.OnRefresh)
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
                    title = "Putaway",
                    onBack = {
                        onEvent(PutawayDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PutawayDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PutawayDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.putaways,
                    itemContent = {_,it->
                        PutawayDetailItem(it){
                            onEvent(PutawayDetailContract.Event.OnSelectPut(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PutawayDetailContract.Event.OnReachEnd)
                    },
                    spacerSize = 7.mdp
                )
            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PutawayDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PutawayDetailContract.Event.OnSortChange(it))
            }
        )
    }
    PutawayBottomSheet(state,onEvent)
}

@Composable
fun PutawayDetailItem(
    model: PutawayListRow,
    onClick: ()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode,R.drawable.keyboard2),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber,R.drawable.barcode),
        item4 = BaseListItemModel("Batch No..",model.batchNumber?:"",R.drawable.vuesax_linear_box),
        item5 = BaseListItemModel("Exp Date",model.expireDateString?:"",R.drawable.calendar_add),
        quantity = model.warehouseLocationCode,
        quantityTitle = "Location",
        scan = model.quantity.removeZeroDecimal().toString(),
        scanTitle = "Quantity"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutawayBottomSheet(
    state: PutawayDetailContract.State,
    onEvent: (PutawayDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedPutaway!=null){

        val locationFocusRequester = remember {
            FocusRequester()
        }
        val barcodeFocusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(Unit) {
            locationFocusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PutawayDetailContract.Event.OnSelectPut(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Putaway",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedPutaway.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.selectedPutaway.productCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.selectedPutaway.productBarcodeNumber,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                if(state.selectedPutaway.batchNumber!=null || state.selectedPutaway.expireDateString!=null)Row(Modifier.fillMaxWidth()) {
                    if(state.selectedPutaway.batchNumber!=null)DetailCard(
                        title = "Batch No.",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedPutaway.batchNumber,
                        modifier = Modifier.weight(1f)
                    )
                    if(state.selectedPutaway.batchNumber!=null && state.selectedPutaway.expireDateString!=null)Spacer(Modifier.size(5.mdp))
                    if(state.selectedPutaway.expireDateString != null)DetailCard(
                        title = "Exp Date",
                        icon = R.drawable.calendar_add,
                        detail = state.selectedPutaway.expireDateString,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Location",
                        icon = R.drawable.location,
                        detail = state.selectedPutaway.warehouseLocationCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedPutaway.quantity.removeZeroDecimal().toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Location Code"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.location,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeLocation(it))
                    },
                    onAny = {
                        barcodeFocusRequester.requestFocus()
                    },
                    leadingIcon = R.drawable.location,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = locationFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Barcode")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PutawayDetailContract.Event.OnSelectPut(null))
                            }
                        },
                        title = "Cancel",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(PutawayDetailContract.Event.OnSavePutaway(state.selectedPutaway))
                        },
                        title = "Save",
                        isLoading = state.onSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PutawayDetailPreview() {
    PutawayDetailContent(
        state = PutawayDetailContract.State(
            selectedPutaway = PutawayListRow(
                batchNumber = "test",
                expireDateString = "today",
                ownerFullName = "mohyeddin",
                productBarcodeNumber = "e342432",
                productCode = "dkfsodif",
                32002,
                productName = "osdkod",
                30220,
                20.0,
                32002,
                20202,
                "A-01-01"
            )
        )
    )

}