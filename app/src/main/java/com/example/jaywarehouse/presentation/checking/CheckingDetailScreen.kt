package com.example.jaywarehouse.presentation.checking

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.checking.models.CheckingListRow
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.presentation.checking.contracts.CheckingDetailContract
import com.example.jaywarehouse.presentation.checking.viewModels.CheckingDetailViewModel
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.ComboBox
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
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.checkRow?.customerName?.trim()?:"",
                    subTitle = "Checking",
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
                    modifier = Modifier.fillMaxSize(),
                    items = state.checkingList,
                    itemContent = {_,it->
                        CheckingDetailItem(it){
                            onEvent(CheckingDetailContract.Event.OnSelectCheck(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(CheckingDetailContract.Event.OnReachEnd)
                    },
                    spacerSize = 7.mdp
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
}


@Composable
fun CheckingDetailItem(
    model: CheckingListRow,
    onClick: ()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode,R.drawable.note),
        item3 = BaseListItemModel("Barcode",model.barcodeNumber?:"",R.drawable.barcode),
        item4 = BaseListItemModel("Reference", model.referenceNumber?:"",R.drawable.hashtag),
        item5 = BaseListItemModel("Quantity", model.quantity.removeZeroDecimal().toString(),R.drawable.vuesax_linear_box),
        showFooter = false,
        quantity = "",
        quantityTitle = "Location",
        scan = model.quantity.removeZeroDecimal().toString(),
        scanTitle = "Quantity"
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
                    text = "Checking",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedChecking.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.note,
                        detail = state.selectedChecking.productCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
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
                        detail = state.selectedChecking.referenceNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
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
                    leadingIcon = R.drawable.location,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = locationFocusRequester,
                    decimalInput = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Pallet(${state.palletMask}-yyMMdd-xxx)")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    onValueChange = {
                        onEvent(CheckingDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    prefix = "${state.palletMask}-",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        TitleView(
                            title = "Pallet Status"
                        )
                        Spacer(Modifier.size(5.mdp))
                        ComboBox(
                            modifier = Modifier,
                            items = state.palletStatusList,
                            selectedItem = state.selectedPalletStatus,
                            icon = R.drawable.vuesax_outline_box_tick,
                            listPadding = PaddingValues(horizontal = 20.mdp),
                            onSelectItem = {
                                onEvent(CheckingDetailContract.Event.OnSelectPalletStatus(it))
                            }
                        )
                    }
                    Spacer(Modifier.size(10.mdp))
                    Column(Modifier.weight(1f)) {
                        TitleView(title = "Pallet Type")
                        Spacer(Modifier.size(5.mdp))
                        ComboBox(
                            modifier = Modifier,
                            items = state.palletTypeList,
                            selectedItem = state.selectedPalletType,
                            icon = R.drawable.box_search,
                            listPadding = PaddingValues(horizontal = 20.mdp),
                            onSelectItem = {
                                onEvent(CheckingDetailContract.Event.OnSelectPalletType(it))
                            }
                        )
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

                            onEvent(CheckingDetailContract.Event.OnCompleteChecking(state.selectedChecking))
                        },
                        title = "Save",
                        isLoading = state.onSaving,
                        enabled = state.count.text.isNotEmpty() && state.barcode.text.isNotEmpty(),
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