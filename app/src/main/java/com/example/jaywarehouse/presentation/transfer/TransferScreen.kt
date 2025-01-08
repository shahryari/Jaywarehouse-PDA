package com.example.jaywarehouse.presentation.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.data.transfer.models.TransferRow
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.DatePickerDialog
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MainListItem
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
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayDetailContract
import com.example.jaywarehouse.presentation.transfer.contracts.TransferContract
import com.example.jaywarehouse.presentation.transfer.viewmodels.TransferViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.example.jaywarehouse.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Destination(style = ScreenTransition::class)
@Composable
fun TransferScreen(
    navigator: DestinationsNavigator,
    viewModel: TransferViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){

                TransferContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    PutawayContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PutawayContent(
    state: TransferContract.State = TransferContract.State(),
    onEvent: (TransferContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }


    val refreshState = rememberPullRefreshState(
        refreshing =  state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(TransferContract.Event.OnRefresh)
        }
    )

    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(TransferContract.Event.ReloadScreen)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(TransferContract.Event.ClearError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(TransferContract.Event.HideToast)
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
                    "Transfer",
                    onBack = {
                        onEvent(TransferContract.Event.OnBackPressed)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(TransferContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(TransferContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    Modifier.fillMaxSize(),
                    items = state.transferList,
                    itemContent = {_,it->
                        TransferItem(it) {
                            onEvent(TransferContract.Event.OnSelectTransfer(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(TransferContract.Event.OnReachedEnd)
                    }
                )
            }

            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(TransferContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(TransferContract.Event.OnChangeSort(it))
            }
        )
    }
    TransferBottomSheet(state,onEvent)
}


@Composable
fun TransferItem(
    model: TransferRow,
    onClick:()->Unit
) {
    MainListItem(
        onClick = onClick,
        typeTitle = model.expireDate,
        modelNumber = model.productBarcodeNumber,
        item1 = BaseListItemModel("Product Name",model.productName, R.drawable.barcode),
        item2 = BaseListItemModel("Location",model.warehouseLocationCode, R.drawable.location),
        totalTitle = "Real",
        total = model.realInventory.toString(),
        countTitle = "Available",
        count = model.availableInventory.toString()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferBottomSheet(
    state: TransferContract.State,
    onEvent: (TransferContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedTransfer!=null){

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(TransferContract.Event.OnSelectTransfer(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Transfer",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Product Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedTransfer.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.selectedTransfer.productBarcodeNumber,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Location",
                        icon = R.drawable.location,
                        detail = state.selectedTransfer.warehouseLocationCode,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Real Inventory",
                        icon = R.drawable.vuesax_outline_box_tick,
                        detail = state.selectedTransfer.realInventory.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Available Inventory",
                        icon = R.drawable.vuesax_outline_box_tick,
                        detail = state.selectedTransfer.availableInventory.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))

                Row(Modifier.fillMaxWidth()) {
                    InputTextField(
                        state.quantity,
                        onValueChange = {
                            onEvent(TransferContract.Event.OnChangeQuantity(it))
                        },
                        onAny = {
                        },
                        label = "Quantity",
                        leadingIcon = R.drawable.box_search,
//                    hideKeyboard = state.lockKeyboard,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.size(5.mdp))
                    AutoDropDownTextField(
                        state.destination,
                        onValueChange = {
                            onEvent(TransferContract.Event.OnChangeDestination(it))
                        },
                        label = "Destination",
                        icon = R.drawable.location,
                        suggestions = state.locationList,
                        onSuggestionClick = {
                            onEvent(TransferContract.Event.OnSelectWarehouseLocation(it))
                        }
//                    hideKeyboard = state.lockKeyboard,
                    )
                }
                Spacer(Modifier.size(10.mdp))
                AutoDropDownTextField(
                    state.productStatus,
                    onValueChange = {
                        onEvent(TransferContract.Event.OnChangeProductStatus(it))
                    },
                    icon = R.drawable.keyboard2,
                    label = "Product Status",
                    suggestions = state.productStatusList,
                    onSuggestionClick = {
                        onEvent(TransferContract.Event.OnSelectProductStatus(it))
                    }
//                    hideKeyboard = state.lockKeyboard,
                )
                Spacer(Modifier.size(10.mdp))
                InputTextField(
                    state.expirationDate,
                    onValueChange = {
                        onEvent(TransferContract.Event.OnChangeExpirationDate(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.calendar_add,
                    keyboardOptions = KeyboardOptions(),
                    onLeadingClick = {
                        onEvent(TransferContract.Event.OnShowDatePicker(true))
                    },
                    readOnly = true,
                    onClick = {
                        onEvent(TransferContract.Event.OnShowDatePicker(true))
                    },
                    label = "Expiration Date",
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(TransferContract.Event.OnSelectTransfer(null))
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

                            onEvent(TransferContract.Event.OnTransfer(state.selectedTransfer))
                        },
                        title = "Save",
//                        isLoading = state.onSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        if (state.showDatePicker) {
            DatePickerDialog(
                onDismiss = {
                    onEvent(TransferContract.Event.OnShowDatePicker(false))
                },
                selectedDate = state.expirationDate.text.ifEmpty { null }
            ) {
                onEvent(TransferContract.Event.OnChangeExpirationDate(TextFieldValue(it)))
                onEvent(TransferContract.Event.OnShowDatePicker(false))

            }
        }
    }
}


@Preview
@Composable
private fun PoutawayPreview() {
    PutawayContent()
}