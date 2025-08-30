package com.linari.presentation.return_receiving

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.return_receiving.models.ReturnDetailRow
import com.linari.data.return_receiving.models.ReturnRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DatePickerDialog
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
import com.linari.presentation.counting.CountingDetailItem
import com.linari.presentation.return_receiving.contracts.ReturnDetailContract
import com.linari.presentation.return_receiving.contracts.ReturnReceivingContract
import com.linari.presentation.return_receiving.viewmodels.ReturnDetailViewModel
import com.linari.ui.theme.ErrorRed
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun ReturnDetailScreen(
    navigator: DestinationsNavigator,
    master: ReturnRow,
    viewModel: ReturnDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(master)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                ReturnDetailContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }

    ReturnDetailContent(state,onEvent)
}

@Composable
fun ReturnDetailContent(
    state: ReturnDetailContract.State = ReturnDetailContract.State(),
    onEvent: (ReturnDetailContract.Event)->Unit = {}
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
        onEvent(ReturnDetailContract.Event.FetchData)
    }

    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ReturnDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(ReturnDetailContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(ReturnDetailContract.Event.OnRefresh)
        }
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = state.master?.receivingNumber?:"",
                    subTitle = "Return",
                    titleTag = state.warehouse?.name ?: "",
                    onBack = {
                        onEvent(ReturnDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(ReturnDetailContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ReturnDetailContract.Event.ShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchRequester
                )

                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.list,
                    state = listState,
                    itemContent = {_,it->
                        ReturnDetailItem(it) {
                            onEvent(ReturnDetailContract.Event.OnSelectForDelete(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(ReturnDetailContract.Event.OnReachEnd)
                    }
                )
            }
            RowCountView(
                Modifier.align(Alignment.BottomCenter),
                current = lastItem.value,
                group = state.list.size,
                total = state.rowCount
            )
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.mdp)
            ){
                FloatingActionButton(
                    onClick = {
                        onEvent(ReturnDetailContract.Event.ShowAdd(true))
                    },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Box(Modifier.padding(13.mdp)) {
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
    if (state.selectedForDelete !=null) {
        ConfirmDialog(
            onDismiss = {
                onEvent(ReturnDetailContract.Event.OnSelectForDelete(null))
            },
            message = "Are you sure to remove [${state.selectedForDelete.referenceNumber}] from return list?",
            isLoading = state.isDeleting
        ) {
            onEvent(ReturnDetailContract.Event.OnConfirmDelete)
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ReturnDetailContract.Event.ShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sortItem,
            onSelectSort = {
                onEvent(ReturnDetailContract.Event.OnSortChange(it))
            }
        )
    }
    AddReturnDetailSheet(state,onEvent)
}

@Composable
fun ReturnDetailItem(
    model: ReturnDetailRow,
    onRemove: ()-> Unit
) {
    var showDetail by remember {
        mutableStateOf(true)
    }
    BaseListItem(
        onClick = {},
        item1 = BaseListItemModel(stringResource(id = R.string.product_name),model.productName,R.drawable.vuesax_outline_3d_cube_scan),
        item2 = if (showDetail) BaseListItemModel(stringResource(id = R.string.product_code),model.productCode,R.drawable.keyboard2) else null,
        item3 = if (showDetail) BaseListItemModel(stringResource(id = R.string.barcode),model.productBarcodeNumber?:"",R.drawable.barcode) else null,
        item4 = if (showDetail) BaseListItemModel(stringResource(id = R.string.product_type),model.quiddityTypeTitle?:"",R.drawable.vuesax_linear_box) else null,
        scan = "",
        scanTitle = "",
        scanContent = {
            MyIcon(
                icon = Icons.Default.Clear,
                background = Color.White,
                onClick = onRemove
            )
        },
        quantity = model.quantity.removeZeroDecimal().toString() + if (model.isWeight == true) " kg" else "",
        quantityTitle = "Quantity"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReturnDetailSheet(
    state: ReturnDetailContract.State,
    onEvent: (ReturnDetailContract.Event)-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showAdd){


        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ReturnDetailContract.Event.ShowAdd(false))
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
                    text = "Add Receiving Detail",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(12.mdp))
                TitleView(
                    title = "Barcode"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    {
                        onEvent(ReturnDetailContract.Event.ChangeBarcode(it))
                    },
                    leadingIcon = R.drawable.barcode,
                    hideKeyboard = state.lockKeyboard
                )
                TitleView(
                    title = "Quantity"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.quantity,
                    onValueChange = {
                        onEvent(ReturnDetailContract.Event.ChangeQuantity(it))
                    },
                    leadingIcon = R.drawable.box_search,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decimalInput = true
                )
                Spacer(Modifier.size(10.mdp))
//                val editDriver = state.selectedDriver == null && state.isDriverIdScanned
                TitleView(title = "Product Status")
                InputTextField(
                    TextFieldValue(state.productStatus?.quiddityTypeTitle?:""),
                    {},
                    leadingIcon = R.drawable.vuesax_outline_box_tick,
                    readOnly = true,
                    onClick = {
                        onEvent(ReturnDetailContract.Event.OnShowStatusList(true))
                    }
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ReturnDetailContract.Event.ShowAdd(false))
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
                            onEvent(ReturnDetailContract.Event.OnAdd)
                        },
                        title = "Save",
                        isLoading = state.isSaving,
                        enabled = state.productStatus!=null&&state.barcode.text.isNotEmpty()&&state.quantity.text.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        ListSheet(
            state.showProductStatusList,
            title = "Product Status List",
            onDismiss = {
                onEvent(ReturnDetailContract.Event.OnShowStatusList(false))
            },
            list = state.productStatusList,
            searchable = true,
            selectedItem = state.productStatus
        ) {
            onEvent(ReturnDetailContract.Event.OnSelectStatus(it))
        }
    }

}