package com.example.jaywarehouse.presentation.shipping

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.shipping.models.PalletInShippingRow
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyButton
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TitleView
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingViewModel
import com.example.jaywarehouse.ui.theme.ErrorRed
import com.example.jaywarehouse.ui.theme.Gray1
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun ShippingScreen(
    navigator: DestinationsNavigator,
    viewModel: ShippingViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){

                ShippingContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    ShippingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShippingContent(
    state: ShippingContract.State = ShippingContract.State(),
    onEvent: (ShippingContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }


    val refreshState = rememberPullRefreshState(
        refreshing =  state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(ShippingContract.Event.OnRefresh)
        }
    )

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(ShippingContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ShippingContract.Event.OnClearError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(ShippingContract.Event.HideToast)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {
                TopBar(
                    stringResource(R.string.shipping),
                    onBack = {
                        onEvent(ShippingContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(ShippingContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ShippingContract.Event.OnShowFilterList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.shippingList,
                    itemContent = {_,it->
                        ShippingItem(
                            it,
                            onPallet = {
                                onEvent(ShippingContract.Event.OnShowPalletQuantitySheet(it))
                            },
                            onConfirm = {
                                onEvent(ShippingContract.Event.OnShowConfirm(it))
                            },
                            onInvoice = {
                                onEvent(ShippingContract.Event.OnShowInvoice(it))
                            },
                            onRs = {
                                onEvent(ShippingContract.Event.OnShowRs(it))

                            }
                        )
                    },
                    onReachEnd = {
                        onEvent(ShippingContract.Event.OnReachEnd)

                    }
                )
            }

            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.mdp)
            ){
                FloatingActionButton(
                    onClick = {
                        onEvent(ShippingContract.Event.OnShowAddDialog(true))
                    },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Box(Modifier.padding(13.mdp)){
                        Icon(
                            painter = painterResource(R.drawable.add_square),
                            contentDescription = "",
                            modifier = Modifier.size(36.mdp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
    if (state.showFilterList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowFilterList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(ShippingContract.Event.OnSortChange(it))
            }
        )
    }
    if (state.confirmShipping!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowConfirm(null))
            },
            message = "Are you sure to confirm shipping ${state.confirmShipping.shippingNumber}?",
            tint = Primary,
            description = "",
        ) {
            onEvent(ShippingContract.Event.OnConfirm(state.confirmShipping))
        }
    }
    if (state.invoiceShipping!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowInvoice(null))
            },
            message = "Are you sure to create invoice for shipping ${state.invoiceShipping.shippingNumber}?",
            tint = Primary,
            description = "",
        ) {
            onEvent(ShippingContract.Event.OnCreateInvoice(state.invoiceShipping))
        }
    }
    if (state.rsShipping!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowConfirm(null))
            },
            message = "Are you sure to create RS for shipping ${state.rsShipping.shippingNumber}",
            tint = Primary,
            description = "",
        ) {
            onEvent(ShippingContract.Event.OnCreateRS(state.rsShipping))
        }
    }
    AddShippingBottomSheet(state,onEvent)
    PalletQuantityBottomSheet(state,onEvent)
}


@Composable
fun ShippingItem(
    model: ShippingRow,
    onPallet: ()->Unit,
    onConfirm: ()->Unit,
    onInvoice: ()->Unit,
    onRs: ()->Unit
) {

    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(15.mdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = model.shippingNumber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
                MyText(
                    text = "#${model.shippingNumber?:""}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )

            }
            Spacer(Modifier.size(10.mdp))
            Row(Modifier.fillMaxWidth()) {
                DetailCard(
                    "Driver ID",
                    icon = R.drawable.vuesax_linear_user_tag,
                    detail = model.driverTin,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(5.mdp))
                DetailCard(
                    "Status",
                    icon = R.drawable.note,
                    detail = model.currentStatusCode,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(10.mdp))
            DetailCard(
                "Driver",
                icon = R.drawable.user_square,
                detail = model.driverFullName
            )
            Spacer(Modifier.size(10.mdp))
            Row(Modifier.fillMaxWidth()) {
                DetailCard(
                    "Car Number",
                    icon = R.drawable.user_square,
                    detail = model.carNumber,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(5.mdp))
                DetailCard(
                    "Trailer Number",
                    icon = R.drawable.note,
                    detail = model.trailerNumber,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(15.mdp))

        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            ShipButton(
                Modifier.weight(1f),
                title = "Pallet",
                icon = R.drawable.pallet,
                background = Color(0xFF416741),
                onClick = onPallet
            )
            AnimatedContent(model.currentStatusCode,label = "") {


                when(it){
                    "Initial"->{
                        ShipButton(
                            Modifier.weight(1f),
                            title = "Confirm",
                            icon = R.drawable.tick,
                            background = Color(0xFF578956),
                            onClick = onConfirm
                        )
                    }
                    "Confirmation"->{
                        ShipButton(
                            Modifier.weight(1f),
                            title = "Invoice",
                            icon = R.drawable.receipt_2,
                            background = Color(0xFF6DAB6C),
                            onClick = onInvoice
                        )
                    }
                    "Invoice"->{
                        ShipButton(
                            Modifier.weight(1f),
                            title = "Send RS",
                            icon = R.drawable.rs,
                            background = Color(0xFF8ABC89),
                            onClick = onRs
                        )
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShippingBottomSheet(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.showAddDialog){



        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingContract.Event.OnShowAddDialog(false))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Create Shipping",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.size(12.mdp))
                TitleView(
                    title = "Driver Id"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.driverId,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnDriverIdChange(it))
                    },
                    onAny = {
                        onEvent(ShippingContract.Event.OnScanDriverId)
                    },
                    leadingIcon = R.drawable.note,
//                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = R.drawable.tick,
                    onTrailingClick = {
                        onEvent(ShippingContract.Event.OnScanDriverId)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                val editDriver = state.selectedDriver == null && state.isDriverIdScanned
                TitleView(title = "Driver FullName")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.driverName,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnDriverNameChange(it))
                    },
                    onAny = {

                    },
                    leadingIcon = R.drawable.user_square,
//                    hideKeyboard = state.lockKeyboard,
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Car Number")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.carNumber,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnCarNumberChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.vuesax_linear_box,
//                    hideKeyboard = state.lockKeyboard,
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Trailer Number")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.trailerNumber,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnTrailerNumberChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.vuesax_linear_box,
//                    hideKeyboard = state.lockKeyboard,
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Pallet Number")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.palletNumber,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnPalletNumberChange(it))
                    },
                    onAny = {
                        onEvent(ShippingContract.Event.OnScanPalletBarcode)
                    },
                    leadingIcon = R.drawable.barcode,
//                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = R.drawable.fluent_barcode_scanner_20_regular,
                    onTrailingClick = {
                        onEvent(ShippingContract.Event.OnScanPalletQuantity)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                LazyColumn(modifier = Modifier.height(140.mdp)) {
                    items(state.createPallets){
                        PalletBarcode(it) {
                            onEvent(ShippingContract.Event.OnRemovePallet(it))
                        }
                        Spacer(Modifier.size(5.mdp))
                    }
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ShippingContract.Event.OnShowAddDialog(false))
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
                            onEvent(ShippingContract.Event.OnAddShipping)
                        },
                        title = "Save",
//                        isLoading = state.isl,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalletQuantityBottomSheet(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.shippingForPallet!=null){


        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingContract.Event.OnShowPalletQuantitySheet(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Create Shipping",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.size(12.mdp))
                TitleView(
                    title = "Customer"
                )
                Spacer(Modifier.size(5.mdp))
                AutoDropDownTextField(
                    state.customer,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnCustomerChange(it))
                    },
                    suggestions = state.customers,
                    icon = R.drawable.user_square,
                    onSuggestionClick = {
                        onEvent(ShippingContract.Event.OnSelectCustomer(it))
                    }
//                    hideKeyboard = state.lockKeyboard,
                )
                Spacer(Modifier.size(10.mdp))
                val editDriver = state.selectedDriver == null && state.isDriverIdScanned
                TitleView(title = "Pallet Type")
                Spacer(Modifier.size(5.mdp))
                AutoDropDownTextField(
                    state.palletType,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnPalletTypeChange(it))
                    },
                    suggestions = state.palletTypes,
                    icon = R.drawable.user_square,
                    onSuggestionClick = {
                        onEvent(ShippingContract.Event.OnSelectPalletType(it))
                    }
//                    hideKeyboard = state.lockKeyboard,
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Quantity")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(ShippingContract.Event.OnQuantityChange(it))
                    },
                    onAny = {
                        onEvent(ShippingContract.Event.OnScanPalletQuantity)
                    },
                    leadingIcon = R.drawable.vuesax_linear_box,
//                    hideKeyboard = state.lockKeyboard,
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = R.drawable.fluent_barcode_scanner_20_regular,
                    onTrailingClick = {
                        onEvent(ShippingContract.Event.OnScanPalletQuantity)
                    }
                )
                Spacer(Modifier.size(15.mdp))
                LazyColumn(modifier = Modifier.height(140.mdp)) {
                    items(state.quantityPallets.filterNot { it.entityState == "Removed" }){
                        PalletQuantity(it) {
                            onEvent(ShippingContract.Event.OnRemovePalletQuantity(it))
                        }
                        Spacer(Modifier.size(5.mdp))
                    }
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ShippingContract.Event.OnShowPalletQuantitySheet(null))
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
                            onEvent(ShippingContract.Event.OnAddPallet)
                        },
                        title = "Save",
//                        isLoading = state.isl,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PalletQuantity(
    model: PalletInShippingRow,
    onRemove: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
            true
        }
    )
    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.mdp))
                    .background(ErrorRed)
                    .padding(10.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.White
                    )
                }
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(1.mdp)
                .clip(RoundedCornerShape(6.mdp))
                .background(Gray1)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = model.customerName?:"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500,
            )
            MyText(
                text = model.palletTypeTitle?:"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.mdp))
                    .background(Primary.copy(0.2f))
                    .padding(vertical = 4.mdp, horizontal = 10.mdp)
            ) {
                MyText(
                    text = model.palletQuantity.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.W500,
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun PalletBarcode(
    model: PalletConfirmRow,
    onRemove: ()->Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
            true
        }
    )
    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.mdp))
                    .background(ErrorRed)
                    .padding(10.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.White
                    )
                }
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "",
                        modifier = Modifier.size(20.mdp),
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(1.mdp)
                .clip(RoundedCornerShape(6.mdp))
                .background(Gray1)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = "#${model.palletBarcode}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W500,
            )
//            Box(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(4.mdp))
//                    .background(Primary.copy(0.2f))
//                    .padding(vertical = 4.mdp, horizontal = 10.mdp)
//            ) {
//                MyText(
//                    text = model.total?.toString() ?: "0",
//                    style = MaterialTheme.typography.labelSmall,
//                    fontWeight = FontWeight.W500,
//                    color = Primary
//                )
//            }
        }
    }
}

@Composable
private fun ShipButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    background: Color,
    onClick: ()->Unit
) {
    Row(
        modifier
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 7.mdp, horizontal = 10.mdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier.size(16.mdp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.size(7.mdp))
        MyText(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W500
        )
    }
}

@Preview
@Composable
private fun ShippingPreview() {
    ShippingContent()
}



















