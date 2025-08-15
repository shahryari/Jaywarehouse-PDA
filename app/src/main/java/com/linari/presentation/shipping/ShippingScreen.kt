package com.linari.presentation.shipping

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linari.data.common.utils.mdp
import com.linari.R
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.data.shipping.models.PalletInShippingRow
import com.linari.data.shipping.models.ShippingDetailListOfPalletRow
import com.linari.data.shipping.models.ShippingPalletManifestRow
import com.linari.data.shipping.models.ShippingRow
import com.linari.presentation.checking.contracts.CheckingDetailContract
import com.linari.presentation.common.composables.AutoDropDownTextField
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.ComboBox
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.ListSheet
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
import com.linari.presentation.destinations.ShippingDetailScreenDestination
import com.linari.presentation.shipping.contracts.ShippingContract
import com.linari.presentation.shipping.contracts.ShippingDetailContract
import com.linari.presentation.shipping.viewmodels.ShippingDetailViewModel
import com.linari.presentation.shipping.viewmodels.ShippingViewModel
import com.linari.ui.theme.Border
import com.linari.ui.theme.ErrorRed
import com.linari.ui.theme.Gray1
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray4
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Orange
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
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

                is ShippingContract.Effect.NavToShippingDetail -> {
                    navigator.navigate(ShippingDetailScreenDestination(it.shipping))
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
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

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
        },
        onRefresh = {
            onEvent(ShippingContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    stringResource(R.string.shipping),
                    titleTag = state.warehouse?.name ?: "",
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
                            onClick = {
                                onEvent(ShippingContract.Event.OnSelectShipping(it))
                            },
                            onPallet = {
                                onEvent(ShippingContract.Event.OnShowPalletQuantitySheet(it))
                            },
                            onConfirm = {
                                onEvent(ShippingContract.Event.OnShowConfirm(it))
                            },
                            onInvoice = {
                                onEvent(ShippingContract.Event.CheckHasPallet(it))
                            },
                            onRollback = {
                                onEvent(ShippingContract.Event.OnShowRollbackConfirm(it))

                            }
                        )
                    },
                    onReachEnd = {
                        onEvent(ShippingContract.Event.OnReachEnd)
                    },
                    state = listState
                )
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.shippingList.size,
                total = state.rowCount
            )
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
            tint = Orange,
            isLoading = state.isConfirming,
            title = "Confirm",
        ) {
            onEvent(ShippingContract.Event.OnConfirm(state.confirmShipping))
        }
    }
    if (state.invoiceShipping!=null){
        com.linari.presentation.common.composables.ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowInvoice(null))
            },
            message = buildAnnotatedString {

                if (state.palletNotInShipping.isNotEmpty()){
                    append("Some pallets for [")
                    withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)){
                        append(
                            state.palletNotInShipping.distinctBy { it.customerName }.joinToString(
                                ","
                            ) { it.customerName }
                        )
                    }
                    append("] are still pending and haven’t been loaded onto a truck.\n")
                    append("[")
                    withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.SemiBold)) {
                        append(
                            state.palletNotInShipping.joinToString(",") { it.palletBarcodes }
                        )
                    }
                    append("]\n")
                    append("Are you sure to dispatch this truck?")
                } else{
                    append("Are you sure to create invoice for shipping ${state.invoiceShipping.shippingNumber}?")
                }
            },
            tint = Orange,
            isLoading = state.isCreatingInvoice,
        ) {
            onEvent(ShippingContract.Event.OnCreateInvoice(state.invoiceShipping))
        }
    }
    if (state.showRollbackConfirm!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowRollbackConfirm(null))
            },
            message = "Are you sure to unconfirm this shipping ${state.showRollbackConfirm.shippingNumber}?",
            tint = Orange,
            isLoading = state.isCreatingRs,
            title = "Confirm",
        ) {
            onEvent(ShippingContract.Event.OnRollbackShipping(state.showRollbackConfirm))
        }
    }
    if (state.showConfirmOfPalletConfirm!=null){
        ConfirmDialog(
            onDismiss = {
                onEvent(ShippingContract.Event.ShowConfirmOfPalletConfirm(null))
            },
            message = "Are you sure to confirm this pallet?",
            isLoading = state.isCreatingPallet
        ) {
            onEvent(ShippingContract.Event.OnConfirmPallet(state.showConfirmOfPalletConfirm))
        }
    }
    AddShippingBottomSheet(state,onEvent)
    PalletQuantityBottomSheet(state,onEvent)
    PalletProductSheet(state,onEvent)
}


@Composable
fun ShippingItem(
    model: ShippingRow,
    onClick: ()->Unit,
    onPallet: ()->Unit,
    onConfirm: ()->Unit,
    onInvoice: ()->Unit,
    onRollback: ()->Unit
) {

    var expend by remember {
        mutableStateOf(false)
    }

    Column(
        Modifier
            .shadow(1.mdp, RoundedCornerShape(10.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.mdp))
            .clickable {
                onClick()
            }
            .background(Color.White)
            .animateContentSize()


    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(15.mdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
//
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = when(model.shippingStatus){
                            0->"confirm"
                            1->"wait for invoice"
                            2->"wait for confirm pallet"
                            else -> ""
                        },
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
            DetailCard(
                "Customer",
                icon = R.drawable.profile_2user,
                detail = model.customerName?.replace(Regex(","),"\n")?:"",
                showFullDetail = true
            )
            Spacer(Modifier.size(10.mdp))
            AnimatedVisibility(expend){
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Driver",
                            icon = R.drawable.user_square,
                            detail = model.driverFullName?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Driver ID",
                            icon = R.drawable.vuesax_linear_user_tag,
                            detail = model.driverTin?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Car No.",
                            icon = R.drawable.truck_next,
                            detail = model.carNumber?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Trailer No.",
                            icon = R.drawable.vuesax_outline_truck_tick,
                            detail = model.trailerNumber?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.mdp))
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            "Pallet Count",
                            icon = R.drawable.box,
                            detail = model.palletCount?.toString()?:"",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(5.mdp))
                        DetailCard(
                            "Product Count",
                            icon = R.drawable.keyboard2,
                            detail = model.sumPalletQty?.removeZeroDecimal()?.toString()?:"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.size(10.mdp))
                    DetailCard(
                        "Date",
                        icon = R.drawable.vuesax_linear_calendar_2,
                        detail = (model.date?:"") +(if (model.date!=null && model.time!=null) "," else "") + (model.time?:"")
                    )
                    Spacer(modifier = Modifier.size(15.mdp))
                }
            }
            Spacer(Modifier.size(5.mdp))
            IconButton(
                onClick = {
                    expend = !expend
                },
                modifier = Modifier.align(Alignment.Start),
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "",
                    modifier = Modifier.rotate(90f),
                    tint = Color.Black
                )
            }

        }
        Row(
            Modifier
                .fillMaxWidth()
        ) {

            AnimatedContent(model.shippingStatus,label = "",modifier = Modifier.fillMaxWidth()) {


                when(it){
                    0->{
                        ShipButton(
                            Modifier.weight(1f),
                            title = "Confirm",
                            icon = R.drawable.tick,
                            background = Gray4,
                            onClick = onConfirm
                        )
                    }
                    1->{
                        Row(Modifier.fillMaxWidth()) {
                            ShipButton(
                                Modifier.weight(1f),
                                title = "Invoice",
                                icon = R.drawable.receipt_2,
                                background = Gray4,
                                shape = RoundedCornerShape(bottomStart = 10.mdp),
                                onClick = onInvoice
                            )
                            ShipButton(
                                Modifier.weight(1f),
                                title = "UnConfirm",
                                icon = R.drawable.tick,
                                background = Gray3,
                                shape = RoundedCornerShape(bottomEnd = 10.mdp),
                                onClick = onRollback
                            )
                        }
                    }
                    2->{
                        ShipButton(
                            Modifier.weight(1f),
                            title = "Confirm Pallet",
                            icon = R.drawable.pallet,
                            background = Gray4,
                            onClick = onPallet
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
                    .verticalScroll(rememberScrollState())
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
                    leadingIcon = R.drawable.user_square,
//                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = R.drawable.tick,
                    onTrailingClick = {
                        onEvent(ShippingContract.Event.OnScanDriverId)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.None)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Driver")
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
                    enabled = state.isDriverIdScanned,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        TitleView(title = "Car No.")
                        Spacer(Modifier.size(5.mdp))
                        InputTextField(
                            state.carNumber,
                            onValueChange = {
                                onEvent(ShippingContract.Event.OnCarNumberChange(it))
                            },
                            onAny = {},
                            leadingIcon = R.drawable.truck_next,
//                    hideKeyboard = state.lockKeyboard,
                            enabled = state.isDriverIdScanned,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Spacer(Modifier.size(10.mdp))
                    Column(Modifier.weight(1f)) {
                        TitleView(title = "Trailer No.")
                        Spacer(Modifier.size(5.mdp))
                        InputTextField(
                            state.trailerNumber,
                            onValueChange = {
                                onEvent(ShippingContract.Event.OnTrailerNumberChange(it))
                            },
                            onAny = {},
                            leadingIcon = R.drawable.vuesax_outline_truck_tick,
//                    hideKeyboard = state.lockKeyboard,
                            enabled = state.isDriverIdScanned,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                    }
                }
                
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Pallet No.")
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
                    loading = state.isChecking,
                    onTrailingClick = {
                        onEvent(ShippingContract.Event.OnScanPalletBarcode)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                LazyColumn(modifier = Modifier.height(140.mdp)) {
                    items(state.createPallets){
                        PalletBarcode(
                            it,
                            onClick = {
                                onEvent(ShippingContract.Event.OnSelectPallet(it))
                            }
                        ) {
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
                        isLoading = state.isShipping,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PalletProductSheet(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.selectedPallet!=null){
        LaunchedEffect(Unit) {
            onEvent(ShippingContract.Event.FetchPalletProducts(state.selectedPallet))
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingContract.Event.OnSelectPallet(null))
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
                        text = "Pallet Details",
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
                        }
                    )
                    Spacer(Modifier.size(15.mdp))
                }
                if (state.isProductLoading) CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePalletQuantity(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showUpdatePallet!=null){


        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingContract.Event.OnShowUpdatePallet(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Update Pallet Quantity",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.size(12.mdp))
                DetailCard(
                    "Customer Name",
                    state.showUpdatePallet.customerName?:"",
                    icon = R.drawable.user_square,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    DetailCard(
                        "Pallet Type",
                        state.showUpdatePallet.palletTypeTitle?:"",
                        icon =R.drawable.box,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    DetailCard(
                        "Pallet Status",
                        state.showUpdatePallet.palletStatusTitle?:"",
                        icon =R.drawable.vuesax_outline_box_tick,
                        modifier = Modifier.weight(1f)
                    )

                }
                Spacer(Modifier.size(10.mdp))
                DetailCard(
                    "Quantity",
                    state.showUpdatePallet.palletQuantity.toString(),
                    icon = R.drawable.vuesax_bulk_box_search,
                    modifier = Modifier.weight(1f)
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
                    },
                    leadingIcon = R.drawable.box_search,
//                    hideKeyboard = state.lockKeyboard,
//                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ShippingContract.Event.OnShowUpdatePallet(null))
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
                            onEvent(ShippingContract.Event.OnUpdatePallet(state.showUpdatePallet))
                        },
                        title = "Save",
                        isLoading = state.isUpdatingPallet,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPalletQuantity(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showAddPallet){


        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ShippingContract.Event.OnShowAddPallet(false))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Add Pallet",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.size(12.mdp))
                TitleView(
                    title = "Customer"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    TextFieldValue(if (state.selectedCustomer!=null)"${(state.selectedCustomer.customerName).trim().trimIndent()}(${state.selectedCustomer.customerCode})" else ""),
                    onValueChange = {
                    },
                    leadingIcon = R.drawable.user_square,
                    readOnly = true,
                    onClick = {
                        onEvent(ShippingContract.Event.OnShowCustomerList(true))
                    }
                )
                Spacer(Modifier.size(10.mdp))
//                val editDriver = state.selectedDriver == null && state.isDriverIdScanned
                TitleView(title = "Pallet Type")
                Spacer(Modifier.size(5.mdp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.mdp))
                        .clickable {
                            onEvent(ShippingContract.Event.OnShowTypeList(true))
                        }
                        .background(color = Color.Transparent)
                        .border(1.mdp, Border,RoundedCornerShape(6.mdp))
                        .padding(vertical = 9.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically
                ){

                    MyIcon(icon = R.drawable.box, showBorder = false, clickable = false)
                    Spacer(modifier = Modifier.size(7.mdp))
                    MyText(
                        state.selectedPalletType?.string()?:"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Pallet Status")
                Spacer(Modifier.size(5.mdp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.mdp))
                        .clickable {
                            onEvent(ShippingContract.Event.OnShowStatusList(true))
                        }
                        .background(color = Color.Transparent)
                        .border(1.mdp, Border,RoundedCornerShape(6.mdp))
                        .padding(vertical = 9.mdp, horizontal = 10.mdp),
                    verticalAlignment = Alignment.CenterVertically
                ){

                    MyIcon(icon = R.drawable.vuesax_outline_box_tick, showBorder = false, clickable = false)
                    Spacer(modifier = Modifier.size(7.mdp))
                    MyText(
                        state.selectedPalletStatus?.string()?:"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
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
                    leadingIcon = R.drawable.box_search,
//                    hideKeyboard = state.lockKeyboard,
//                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    trailingIcon = R.drawable.fluent_barcode_scanner_20_regular,
//                    onTrailingClick = {
//                        onEvent(ShippingContract.Event.OnScanPalletQuantity)
//                    }
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ShippingContract.Event.OnShowAddPallet(false))
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
                            onEvent(ShippingContract.Event.OnScanPalletQuantity)
                        },
                        title = "Save",
                        isLoading = state.isAddingPallet,
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
                    .fillMaxHeight(0.7f)
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Pallet Confirm",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.size(12.mdp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.quantityPallets){
                        PalletQuantity(
                            it,
                            onClick = {
                                onEvent(ShippingContract.Event.OnShowUpdatePallet(it))
                            }
                        ) {
                            onEvent(ShippingContract.Event.OnShowConfirmDeletePallet(it))
                        }
                        Spacer(Modifier.size(5.mdp))
                    }
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier
                    .align(Alignment.End)
                    .weight(1f)) {
                    Button(
                        onClick = {
                            onEvent(ShippingContract.Event.OnShowAddPallet(true))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.mdp),
                        modifier = Modifier
                            .padding( horizontal = 15.mdp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = ""
                        )
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
                            onEvent(ShippingContract.Event.ShowConfirmOfPalletConfirm(state.shippingForPallet))
                        },
                        title = "Confirm",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        AddPalletQuantity(state,onEvent)
        UpdatePalletQuantity(state,onEvent)
        ListSheet(
            state.showTypeList,
            title = "Pallet Type List",
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowTypeList(false))
            },
            list = state.palletTypes,
            selectedItem = state.selectedPalletType,
        ) {
            onEvent(ShippingContract.Event.OnSelectPalletType(it))
        }
        ListSheet(
            state.showStatusList,
            title = "Pallet Status List",
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowStatusList(false))
            },
            list = state.palletStatusList,
            selectedItem = state.selectedPalletStatus,
        ) {
            onEvent(ShippingContract.Event.OnSelectPalletStatus(it))
        }
        ListSheet(
            state.showCustomerList,
            title = "Customer List",
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowCustomerList(false))
            },
            list = state.customers,
            searchable = true,
            selectedItem = state.selectedCustomer
        ) {
            onEvent(ShippingContract.Event.OnSelectCustomer(it))
        }

        if (state.showConfirmDeletePallet!=null){
            ConfirmDialog(
                onDismiss = {
                    onEvent(ShippingContract.Event.OnShowConfirmDeletePallet(null))
                },
                onConfirm = {
                    onEvent(ShippingContract.Event.OnRemovePalletQuantity(state.showConfirmDeletePallet))
                },
                title = "Delete Pallet",
                isLoading = state.isDeletingPallet,
                message = "Are you sure you want to delete this pallet?"
            )
        }
    }
}

@Composable
fun PalletQuantity(
    model: PalletInShippingRow,
    onClick: ()-> Unit,
    onRemove: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    true
                }
            }
        }
    )
    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Row(
                Modifier
                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.mdp))
                    .background(ErrorRed)
                    .padding(horizontal = 10.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            modifier = Modifier.size(20.mdp),
                            tint = Color.White
                        )
                        Spacer(Modifier.size(10.mdp))
                        MyText(
                            "Remove",
                            lineHeight = 13.sp,

                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MyText(
                            "Remove",
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Spacer(Modifier.size(10.mdp))
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            modifier = Modifier.size(20.mdp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) {
        Row(
            Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.mdp))
                .clickable {
                    onClick()
                }
                .background(Gray1)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MyText(
                text = model.customerName?:"",
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.weight(2.5f),
                overflow = TextOverflow.Ellipsis
            )
            MyText(
                text = model.palletTypeTitle?:"",
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.weight(1f)
            )
            MyText(
                text = model.palletStatusTitle?:"",
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.weight(1f)
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
    model: ShippingPalletManifestRow,
    onClick: ()-> Unit,
    onRemove: ()->Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {it*0.25f},
        confirmValueChange = {
            when(it){
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    false
                }
                SwipeToDismissBoxValue.Settled -> {
                    true
                }
            }
        }
    )
    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Row(
                Modifier
                    .shadow(1.mdp, RoundedCornerShape(6.mdp))
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.mdp))
                    .background(ErrorRed)
                    .padding(horizontal = 10.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Spacer(Modifier.size(5.mdp))
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            modifier = Modifier.size(20.mdp),
                            tint = Color.White
                        )
                        Spacer(Modifier.size(10.mdp))
                        MyText(
                            "Remove",
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                AnimatedVisibility(swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MyText(
                            "Remove",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 13.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.size(10.mdp))
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            modifier = Modifier.size(20.mdp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) {
        Row(
            Modifier
                .shadow(1.mdp, RoundedCornerShape(6.mdp))
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.mdp))
                .clickable {
                    onClick()
                }
                .background(Gray1)
                .padding(vertical = 6.mdp, horizontal = 8.mdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                MyText(
                    text = "#${model.palletBarcode}",
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.W500,
                )
                Spacer(Modifier.size(3.mdp))
                MyText(
                    text = "${model.customerName?:""}(${model.customerCode?:""})",
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.Black.copy(0.8f)
                )

            }
            if (model.total !=null){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.mdp))
                        .background(Primary.copy(0.2f))
                        .padding(vertical = 4.mdp, horizontal = 10.mdp)
                ) {
                    MyText(
                        text = model.total?.toString() ?: "0",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.W500,
                        color = Primary
                    )
                }
            }
        }
    }
}
@Composable
private fun ShipButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int,
    background: Color,
    shape: Shape = RoundedCornerShape(bottomStart = 10.mdp, bottomEnd = 10.mdp),
    onClick: ()->Unit
) {
    Row(
        modifier
            .shadow(1.mdp,shape)
            .clip(shape)
            .background(background)
            .border(1.mdp,Border,shape)
            .clickable { onClick() }
            .padding(vertical = 7.mdp, horizontal = 10.mdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier.size(16.mdp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.size(7.mdp))
        MyText(
            text = title,
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
private fun ShippingPreview() {
    ShippingContent()
}



















