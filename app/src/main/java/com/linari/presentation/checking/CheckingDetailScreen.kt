package com.linari.presentation.checking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.checking.models.CheckingListGroupedRow
import com.linari.data.checking.models.CheckingListRow
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.presentation.checking.contracts.CheckingDetailContract
import com.linari.presentation.checking.viewModels.CheckingDetailViewModel
import com.linari.presentation.common.composables.AutoDropDownTextField
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.ComboBox
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.ListSheet
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyCheckBox
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
import com.linari.presentation.destinations.DashboardScreenDestination
import com.linari.presentation.destinations.PutawayScreenDestination
import com.linari.presentation.shipping.contracts.ShippingContract
import com.linari.ui.theme.Border
import com.linari.ui.theme.Gray1
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun CheckingDetailScreen(
    navigator: DestinationsNavigator,
    checkingRow: CheckingListGroupedRow,
    viewModel: CheckingDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(checkingRow)
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
                CheckingDetailContract.Effect.NavBack -> navigator.popBackStack()
                CheckingDetailContract.Effect.NavToDashboard -> {
                    navigator.navigate(DashboardScreenDestination){
                        popUpTo(PutawayScreenDestination){
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
    CheckingDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckingDetailContent(
    state: CheckingDetailContract.State = CheckingDetailContract.State(),
    onEvent: (CheckingDetailContract.Event)->Unit = {}
) {
    val focusRequester = FocusRequester()

    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(CheckingDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(CheckingDetailContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(CheckingDetailContract.Event.OnRefresh)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .weight(1f)
                        .padding(15.mdp)
                ) {
                    TopBar(
                        title = state.checkRow?.customerName?.trim()?:"",
                        subTitle = stringResource(id = R.string.checking),
                        onBack = {
                            onEvent(CheckingDetailContract.Event.OnNavBack)
                        }
                    )
                    Spacer(modifier = Modifier.size(20.mdp))
                    SearchInput(
                        onSearch = {
                            onEvent(CheckingDetailContract.Event.OnSearch(it.text))
                        },
                        value = state.keyword,
                        isLoading = state.loadingState == Loading.SEARCHING,
                        onSortClick = {
                            onEvent(CheckingDetailContract.Event.OnShowSortList(true))
                        },
                        hideKeyboard = state.lockKeyboard,
                        focusRequester = focusRequester
                    )

                    Spacer(modifier = Modifier.size(20.mdp))
                    MyLazyColumn(
                        modifier = Modifier.weight(1f),
                        items = state.checkingList,
                        itemContent = {_,it->
                            CheckingDetailItem(
                                it,
                                state.hasPickCancel,
                                {
                                    onEvent(CheckingDetailContract.Event.OnSelectCheck(it))
                                }
                            ) {
                                onEvent(CheckingDetailContract.Event.SelectForCancel(it))
                            }
                        },
                        state = listState,
                        onReachEnd = {
                            onEvent(CheckingDetailContract.Event.OnReachEnd)
                        },
                        spacerSize = 7.mdp
                    )

                }
                RowCountView(
                    current = lastItem.value,
                    group = state.checkingList.size,
                    total = state.rowCount
                )
            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(CheckingDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(CheckingDetailContract.Event.OnSortChange(it))
            }
        )
    }
    CheckingBottomSheet(state,onEvent)
    CancelChecking(state,onEvent)
    ListSheet(
        state.showTypeList,
        title = stringResource(id = R.string.pallet_type_list),
        onDismiss = {
            onEvent(CheckingDetailContract.Event.ShowTypeList(false))
        },
        list = state.palletTypeList,
        selectedItem = state.selectedPalletType,
    ) {
        onEvent(CheckingDetailContract.Event.OnSelectPalletType(it))
    }
    ListSheet(
        state.showStatusList,
        title = stringResource(id = R.string.pallet_status_list),
        onDismiss = {
            onEvent(CheckingDetailContract.Event.ShowStatusList(false))
        },
        list = state.palletStatusList,
        selectedItem = state.selectedPalletStatus,
    ) {
        onEvent(CheckingDetailContract.Event.OnSelectPalletStatus(it))
    }
}


@Composable
fun CheckingDetailItem(
    model: CheckingListRow,
    hasCancel: Boolean = false,
    onClick: ()->Unit,
    onRemove:()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel(stringResource(id = R.string.product_name),model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel(stringResource(id = R.string.product_code),model.productCode,R.drawable.keyboard2),
        item3 = BaseListItemModel( stringResource(id = R.string.barcode),model.barcodeNumber?:"",R.drawable.barcode),
        item4 = BaseListItemModel( stringResource(id = R.string.reference_no), model.referenceNumber?:"",R.drawable.hashtag),
//        item5 = BaseListItemModel(stringResource(R.string.quantity), model.quantity.removeZeroDecimal().toString(),R.drawable.vuesax_linear_box),
        quantityTitle = stringResource(R.string.quantity),
        quantity = model.quantity.removeZeroDecimal(),
        scan = "",
        scanTitle = "",
        scanContent = {
            if (hasCancel)MyIcon(
                icon = Icons.Default.Clear,
                onClick = onRemove
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckingBottomSheet(
    state: CheckingDetailContract.State,
    onEvent: (CheckingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedChecking!=null){

        val locationFocusRequester = remember {
            FocusRequester()
        }
        val barcodeFocusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(Unit) {
            delay(200)
            locationFocusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(CheckingDetailContract.Event.OnSelectCheck(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.mdp)
                    .padding(bottom = 20.mdp)
            ){
                MyText(
                    text = stringResource(id = R.string.checking),
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = stringResource(id = R.string.product_name),
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedChecking.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = stringResource(id = R.string.product_code),
                        icon = R.drawable.note,
                        detail = state.selectedChecking.productCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = stringResource(id = R.string.barcode),
                        icon = R.drawable.barcode,
                        detail = state.selectedChecking.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference",
                        icon = R.drawable.hashtag,
                        detail = state.selectedChecking.purchaseOrderReferenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = stringResource(id = R.string.quantity),
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedChecking.quantity.removeZeroDecimal().toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Quantity"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.count,
                    onValueChange = {
                        onEvent(CheckingDetailContract.Event.OnChangeLocation(it))
                    },
                    onAny = {
                        barcodeFocusRequester.requestFocus()
                    },
                    leadingIcon = R.drawable.box_search,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = locationFocusRequester,
                    decimalInput = true,

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "${stringResource(R.string.pallet)}(${state.palletMask}-yyMMdd-xxx)")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    onValueChange = {
                        onEvent(CheckingDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    prefix = "${state.palletMask}-",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        TitleView(
                            title = stringResource(R.string.pallet_status),
                        )
                        Spacer(Modifier.size(5.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.mdp))
                                .clickable(!state.statusLock) {
                                    onEvent(CheckingDetailContract.Event.ShowStatusList(true))
                                }
                                .background(color = if (state.statusLock) Gray1 else Color.Transparent)
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
                    }
                    Spacer(Modifier.size(10.mdp))
                    Column(Modifier.weight(1f)) {
                        TitleView(title = stringResource(R.string.pallet_type))
                        Spacer(Modifier.size(5.mdp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.mdp))
                                .background(color = if (state.typeLock) Gray1 else Color.Transparent)
                                .clickable(!state.typeLock) {
                                    onEvent(CheckingDetailContract.Event.ShowTypeList(true))
                                }
                                .border(1.mdp, Border,RoundedCornerShape(6.mdp))
                                .padding(vertical = 9.mdp, horizontal = 10.mdp),
                            verticalAlignment = Alignment.CenterVertically
                        ){

                            MyIcon(icon = R.drawable.vuesax_outline_box_tick, showBorder = false, clickable = false)
                            Spacer(modifier = Modifier.size(7.mdp))
                            MyText(
                                state.selectedPalletType?.string()?:"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(CheckingDetailContract.Event.OnSelectCheck(null))
                            }
                        },
                        title = stringResource(R.string.cancel),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(CheckingDetailContract.Event.OnCompleteChecking(state.selectedChecking))
                        },
                        title = stringResource(R.string.save),
                        isLoading = state.onSaving,
                        enabled = state.count.text.isNotEmpty() && state.barcode.text.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelChecking(
    state: CheckingDetailContract.State,
    onEvent: (CheckingDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedForCancel!=null){

        val locationFocusRequester = remember {
            FocusRequester()
        }
        val quantityFocusRequester = remember {
            FocusRequester()
        }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            delay(200)
            quantityFocusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(CheckingDetailContract.Event.SelectForCancel(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.mdp)
                    .padding(bottom = 20.mdp)
            ){
                MyText(
                    text = "Cancel Picking",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = stringResource(id = R.string.product_name),
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedForCancel.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = stringResource(id = R.string.product_code),
                        icon = R.drawable.note,
                        detail = state.selectedForCancel.productCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = stringResource(id = R.string.barcode),
                        icon = R.drawable.barcode,
                        detail = state.selectedForCancel.barcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Reference",
                        icon = R.drawable.hashtag,
                        detail = state.selectedForCancel.purchaseOrderReferenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = stringResource(id = R.string.quantity),
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedForCancel.quantity.removeZeroDecimal().toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.selectedForCancel.locationCode!=null){
                    Spacer(Modifier.size(10.mdp))
                    Row(Modifier.fillMaxWidth()) {
                        DetailCard(
                            title = "Location",
                            icon = R.drawable.location,
                            detail = state.selectedForCancel.locationCode?:"",
                            modifier = Modifier.weight(1f)
                        )
//                    Spacer(Modifier.size(5.mdp))
//                    DetailCard(
//                        title = stringResource(id = R.string.quantity),
//                        icon = R.drawable.vuesax_linear_box,
//                        detail = state.selectedForCancel.quantity.removeZeroDecimal().toString(),
//                        modifier = Modifier.weight(1f)
//                    )
                    }
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Quantity")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.cancelQuantity,
                    onValueChange = {
                        onEvent(CheckingDetailContract.Event.OnChangeCancelQuantity(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = quantityFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(10.mdp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MyCheckBox(
                        checked = state.isDamaged,
                        size = 18.mdp
                    ) {
                        onEvent(CheckingDetailContract.Event.OnChangeIsDamaged(it))
                    }

                    Spacer(Modifier.size(5.mdp))
                    MyText(
                        text = "Is Damaged",
                        fontSize = 15.sp
                    )
                }
                if (state.locationBase) {
                    Spacer(Modifier.size(10.mdp))
                    TitleView(
                        title = "Destination Location",
                    )
                    Spacer(Modifier.size(5.mdp))
                    InputTextField(
                        state.cancelLocation,
                        onValueChange = {
                            onEvent(CheckingDetailContract.Event.OnChangeCancelLocation(it))
                        },
                        onAny = {
                        },
                        leadingIcon = R.drawable.location,
                        focusRequester = locationFocusRequester,
//                        decimalInput = true,
//                        hideKeyboard = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(CheckingDetailContract.Event.SelectForCancel(null))
                            }
                        },
                        title = stringResource(R.string.cancel),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray3,
                            contentColor = Gray5
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(10.mdp))
                    MyButton(
                        onClick = {

                            onEvent(CheckingDetailContract.Event.OnCancelChecking(state.selectedForCancel))
                        },
                        title = stringResource(R.string.save),
                        isLoading = state.isCanceling,
                        enabled = (if (state.locationBase)state.cancelLocation.text.isNotEmpty() else true) && state.cancelQuantity.text.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CheckingDetailPreview() {
    CheckingDetailContent()

}