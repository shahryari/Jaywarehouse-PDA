package com.example.jaywarehouse.presentation.pallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.pallet.model.PalletManifestProductRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.ui.theme.Green
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun PalletScreen(
    navigator: DestinationsNavigator,
    viewModel: PalletConfirmViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){

                PalletConfirmContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    PalletContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PalletContent(
    state: PalletConfirmContract.State = PalletConfirmContract.State(),
    onEvent: (PalletConfirmContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }


    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PalletConfirmContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PalletConfirmContract.Event.ClearError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(PalletConfirmContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(PalletConfirmContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = "Pallet Complete",
                    onBack = {
                        onEvent(PalletConfirmContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(PalletConfirmContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PalletConfirmContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    Modifier
                    .fillMaxSize(),
                    items = state.palletList,
                    itemContent = {_,it->
                        PalletItem(
                            it,
                            onClick = {
                                onEvent(PalletConfirmContract.Event.OnShowPalletProduct(it))
                            }
                        ) {
                            onEvent(PalletConfirmContract.Event.OnSelectPallet(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PalletConfirmContract.Event.OnReachedEnd)
                    }
                )
                Spacer(modifier = Modifier.size(70.mdp))
            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PalletConfirmContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PalletConfirmContract.Event.OnChangeSort(it))
            }
        )
    }
    if (state.selectedPallet!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(PalletConfirmContract.Event.OnSelectPallet(null))
            },
            message = "Are you sure to confirm this pallet ${state.selectedPallet.palletBarcode}?",
            title = "Confirm Pallet",
            tint = Orange,
            isLoading = state.isConfirming,
            onConfirm = {
                onEvent(PalletConfirmContract.Event.ConfirmPallet(state.selectedPallet))
            }
        ) 
    }
    PalletProductSheet(state,onEvent)
}


@Composable
fun PalletItem(
    model: PalletConfirmRow,
    onClick: ()->Unit,
    onSelect: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onSelect()
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    true
                }
            }
        }
    )
    LaunchedEffect(swipeState.currentValue) {
        swipeState.reset()
    }
    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Column(
                Modifier
                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
                    .fillMaxWidth()
                    .fillMaxHeight()

                    .clip(RoundedCornerShape(6.mdp))
                    .background(Green)
                    .padding(15.mdp),
                verticalArrangement = Arrangement.Center,
            ){
                Icon(
                    Icons.Default.Check,
                    contentDescription = "",
                    Modifier
                        .size(30.mdp),
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.mdp))
                .clickable {
                    onClick()
                }
                .background(Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(15.mdp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    if(model.customerCode!=null)Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.mdp))
                            .background(Primary.copy(0.2f))
                            .padding(vertical = 4.mdp, horizontal = 10.mdp)
                    ) {
                        MyText(
                            text = model.customerCode,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    } else  {
                        Spacer(Modifier.size(10.mdp))
                    }
                    MyText(
                        text = "#${model.palletBarcode?:""}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )

                }
                Spacer(modifier = Modifier.size(10.mdp))
                DetailCard(
                    "Customer",
                    icon = R.drawable.user_square,
                    detail = model.customerName?:""
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalletProductSheet(
    state: PalletConfirmContract.State,
    onEvent: (PalletConfirmContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showPalletProducts!=null){
        LaunchedEffect(Unit) {
            onEvent(PalletConfirmContract.Event.FetchProducts(state.showPalletProducts))
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PalletConfirmContract.Event.OnShowPalletProduct(null))
            },
            containerColor = Color.White
        ) {
            Box {

                Column (
                    Modifier
                        .padding(horizontal = 24.mdp)
                        .padding(bottom = 24.mdp)
                ) {
                    MyText(
                        text = "Pallet Products",
                        fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.size(12.mdp))
                    MyLazyColumn(
                        items = state.palletProducts,
                        itemContent = { _, it ->
                            PalletProduct(it)
                        },
                        onReachEnd = {
                            onEvent(PalletConfirmContract.Event.OnProductsReachEnd(state.showPalletProducts))
                        }
                    )
                    Spacer(Modifier.size(15.mdp))
//                    MyButton(
//                        onClick = {
//                            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                                onEvent(PalletConfirmContract.Event.OnShowPalletProduct(null))
//                            }
//                        },
//                        title = "Close",
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Gray3,
//                            contentColor = Gray5
//                        ),
//                    )
                }
                if (state.productLoading == Loading.LOADING) CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
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
        mutableStateOf(false)
    }
    BaseListItem(
        onClick = {
            expended = !expended
        },
        scan = null,
        quantity = model.quantity?.removeZeroDecimal()?:"",
        scanTitle = "Check Quantity",
        quantityTitle = "Quantity",
        item1 = BaseListItemModel("Product Name",model.productName?:"",R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (expended)BaseListItemModel("Product Code",model.productCode?:"",R.drawable.note) else null,
        item3 = if (expended)BaseListItemModel("Barcode",model.barcode?:"",R.drawable.barcode) else null
    )
}

@Preview
@Composable
private fun PalletPreview() {
    var selected by remember {
        mutableStateOf(false)
    }
    Column {
    }
}