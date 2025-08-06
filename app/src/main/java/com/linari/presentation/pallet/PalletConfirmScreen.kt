package com.linari.presentation.pallet

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MainListItem
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyIcon
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
import com.linari.presentation.destinations.PalletProductScreenDestination
import com.linari.presentation.pallet.contracts.PalletConfirmContract
import com.linari.presentation.pallet.contracts.PalletProductContract
import com.linari.presentation.pallet.viewmodels.PalletConfirmViewModel
import com.linari.ui.theme.Background
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Green
import com.linari.ui.theme.Orange
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
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

                is PalletConfirmContract.Effect.NavToDetail -> {
                    navigator.navigate(PalletProductScreenDestination(it.pallet))
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
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
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
                                onEvent(PalletConfirmContract.Event.OnNavToDetail(it))
                            }
                        ) {
                            onEvent(PalletConfirmContract.Event.OnSelectPallet(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(PalletConfirmContract.Event.OnReachedEnd)
                    },
                    state = listState,
                )
                Spacer(modifier = Modifier.size(70.mdp))
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.palletList.size,
                total = state.rowCount
            )
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
    if (state.hasBoxOnShipping){
        BoxSheet(state,onEvent)
    } else {
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
    }
}


@Composable
fun PalletItem(
    model: PalletConfirmRow,
    onClick: ()->Unit,
    showComplete: Boolean = true,
    onSelect: () -> Unit
) {
//    val swipeState = rememberSwipeToDismissBoxState(
//        positionalThreshold = {it*0.25f},
//        confirmValueChange = {
//            when(it){
//                SwipeToDismissBoxValue.StartToEnd,
//                SwipeToDismissBoxValue.EndToStart -> {
//                    onSelect()
//                    false
//                }
//                SwipeToDismissBoxValue.Settled -> {
//                    true
//                }
//            }
//        }
//    )
//    LaunchedEffect(swipeState.currentValue) {
//        swipeState.reset()
//    }
//    SwipeToDismissBox(
//        state = swipeState,
//        enableDismissFromEndToStart = false,
//        backgroundContent = {
//            Row(
//                Modifier
//                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
//                    .fillMaxWidth()
//                    .fillMaxHeight()
//
//                    .clip(RoundedCornerShape(6.mdp))
//                    .background(Primary)
//                    .padding(15.mdp),
//                verticalAlignment = Alignment.CenterVertically,
//            ){
//
//                Icon(
//                    Icons.Default.Check,
//                    contentDescription = "",
//                    Modifier
//                        .size(30.mdp),
//                    tint = Color.White
//                )
//                Spacer(Modifier.size(10.mdp))
//                MyText(
//                    text = "Complete",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.W500,
//                    color = Color.White
//                )
//            }
//        }
//    ) {
//    }
    MainListItem(
        onClick = onClick,
        typeTitle = model.customerCode,
        modelNumber = model.palletBarcode,
        item1 = BaseListItemModel("Customer",model.customerName?:"",R.drawable.user_square),
        total = model.total?.removeZeroDecimal()?:"0",
        count = "",
        countTitle = "",
        countIcon = null,
        countContent = {
            if (showComplete)MyIcon(
                icon = Icons.Default.Check,
                tint = Primary,
                background = Color.White,
                onClick = onSelect
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxSheet(state: PalletConfirmContract.State, onEvent: (PalletConfirmContract.Event)->Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    if (state.selectedPallet!=null)
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(PalletConfirmContract.Event.OnSelectPallet(null))
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
                Spacer(Modifier.size(15.mdp))
                DetailCard(
                    "Customer",
                    state.selectedPallet.customerName?:"",
                    icon = R.drawable.vuesax_linear_user,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        "Customer Code",
                        state.selectedPallet.customerCode?:"",
                        icon = R.drawable.vuesax_linear_user,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        "Pallet Barcode",
                        state.selectedPallet.palletBarcode?:"",
                        icon = R.drawable.vuesax_linear_box,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Big Box Quantity"
                )
                InputTextField(
                    state.bigQuantity,
                    onValueChange = {
                        onEvent(PalletConfirmContract.Event.ChangeBigQuantity(it))
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
                        onEvent(PalletConfirmContract.Event.ChangeSmallQuantity(it))
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
                                onEvent(PalletConfirmContract.Event.OnSelectPallet(null))
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

                            onEvent(PalletConfirmContract.Event.OnConfirmBox(state.selectedPallet))
                        },
                        title = "Complete",
                        isLoading = state.isConfirming,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
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