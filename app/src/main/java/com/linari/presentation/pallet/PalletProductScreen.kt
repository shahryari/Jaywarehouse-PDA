package com.linari.presentation.pallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.RowCountView
import com.linari.presentation.common.composables.SearchInput
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TitleView
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.pallet.contracts.PalletConfirmContract
import com.linari.presentation.pallet.contracts.PalletProductContract
import com.linari.presentation.pallet.viewmodels.PalletProductViewModel
import com.linari.presentation.picking.contracts.PickingListBDContract
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun PalletProductScreen(
    navigator: DestinationsNavigator,
    pallet: PalletConfirmRow,
    viewModel: PalletProductViewModel = koinViewModel(
        parameters = {
            parametersOf(pallet)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                PalletProductContract.Effect.NavBack -> navigator.popBackStack()
            }
        }
    }
    PalletProductContent(state,onEvent)
}

@Composable
fun PalletProductContent(
    state: PalletProductContract.State = PalletProductContract.State(),
    onEvent: (PalletProductContract.Event)->Unit = {}
) {
    val focusRequester = remember {
        FocusRequester()
    }
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(SIDE_EFFECT_KEY) {
        focusRequester.requestFocus()
        onEvent(PalletProductContract.Event.FetchData)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        toast = state.toast,
        onCloseError = {
            onEvent(PalletProductContract.Event.CloseError)
        },
        onHideToast = {
            onEvent(PalletProductContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(PalletProductContract.Event.OnRefresh)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.pallet?.customerName?:"",
                    subTitle = "Pallet Details",
                    onBack = {
                        onEvent(PalletProductContract.Event.OnNavBack)
                    },
                    endIcon = R.drawable.tick,
                    onEndClick = {
                        onEvent(PalletProductContract.Event.OnShowConfirm(true))
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PalletProductContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PalletProductContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    Modifier
                        .fillMaxSize(),
                    items = state.productList,
                    itemContent = {_,it->
                        PalletProduct(it)
                    },
                    onReachEnd = {
                        onEvent(PalletProductContract.Event.OnReachEnd)
                    },
                    header ={
                        if (state.pallet!=null) {
                            Column {
                                PalletItem(
                                    state.pallet,
                                    showComplete = false,
                                    onClick = {

                                    },
                                    onSelect = {}
                                )
                                Spacer(Modifier.size(10.mdp))

                            }
                        }
                    },
                    state = listState,
                )
                Spacer(modifier = Modifier.size(70.mdp))
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.productList.size,
                total = state.rowCount
            )
        }
    }

    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PalletProductContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PalletProductContract.Event.OnSelectSort(it))
            }
        )
    }
    if (state.hasBoxOnShipping){
        BoxSheet(state,onEvent)
    } else {
        if (state.showConfirm){
            ConfirmDialog(
                onDismiss = {
                    onEvent(PalletProductContract.Event.OnShowConfirm(false))
                },
                message = "Are you sure to complete this pallet?",
                title = "Confirm Pallet",
                tint = Orange,
                isLoading = state.isConfirming,
                onConfirm = {
                    onEvent(PalletProductContract.Event.OnConfirm)
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxSheet(state: PalletProductContract.State, onEvent: (PalletProductContract.Event)->Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.showConfirm)
    ModalBottomSheet(
        onDismissRequest = {
            onEvent(PalletProductContract.Event.OnShowConfirm(false))
        },
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.mdp)
                .padding(bottom = 24.mdp)
        ) {
            MyText(
                text = "Pallet Manifest Box",
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.size(10.mdp))
            TitleView(
                title = "Big Box Quantity"
            )
            InputTextField(
                state.bigQuantity,
                onValueChange = {
                    onEvent(PalletProductContract.Event.ChangeBigQuantity(it))
                },
                decimalInput = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(Modifier.size(10.mdp))
            TitleView(
                title = "Small Box Quantity"
            )
            InputTextField(
                state.smallQuantity,
                onValueChange = {
                    onEvent(PalletProductContract.Event.ChangeSmallQuantity(it))
                },
                decimalInput = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(Modifier.size(10.mdp))
            Spacer(Modifier.size(15.mdp))
            Row(Modifier.fillMaxWidth()) {
                MyButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onEvent(PalletProductContract.Event.OnShowConfirm(false))
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

                        onEvent(PalletProductContract.Event.OnConfirmBox)
                    },
                    title = "Complete",
                    isLoading = state.isConfirming,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
@Composable
fun PalletProduct(
    model: PalletManifestProductRow
) {
    var expended by remember {
        mutableStateOf(true)
    }
    BaseListItem(
        onClick = {
            expended = !expended
        },
        scan = null,
        quantity = model.quantity?.removeZeroDecimal()?:"",
        quantityTitle = "Quantity",
        item1 = BaseListItemModel("Product Name",model.productName?:"",R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (expended)BaseListItemModel("Product Code",model.productCode?:"",R.drawable.keyboard2) else null,
        item3 = if (expended)BaseListItemModel("Barcode",model.barcode?:"",R.drawable.barcode) else null
    )
}