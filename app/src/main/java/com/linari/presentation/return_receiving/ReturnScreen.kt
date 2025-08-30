package com.linari.presentation.return_receiving

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.return_receiving.models.ReturnRow
import com.linari.presentation.common.composables.BaseListItem
import com.linari.presentation.common.composables.BaseListItemModel
import com.linari.presentation.common.composables.DatePickerDialog
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.ListSheet
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
import com.linari.presentation.counting.CountListItem
import com.linari.presentation.counting.contracts.CountingContract
import com.linari.presentation.destinations.ReturnDetailScreenDestination
import com.linari.presentation.return_receiving.contracts.ReturnDetailContract
import com.linari.presentation.return_receiving.contracts.ReturnReceivingContract
import com.linari.presentation.return_receiving.viewmodels.ReturnViewModel
import com.linari.presentation.shipping.contracts.ShippingContract
import com.linari.ui.theme.Border
import com.linari.ui.theme.ErrorRed
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Destination(style = ScreenTransition::class)
@Composable
fun ReturnScreen(
    navigator: DestinationsNavigator,
    viewModel: ReturnViewModel = koinViewModel(),
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it) {
                ReturnReceivingContract.Effect.NavBack -> navigator.popBackStack()
                is ReturnReceivingContract.Effect.NavToDetail -> {
                    navigator.navigate(ReturnDetailScreenDestination(it.model))
                }
            }
        }
    }

    ReturnContent(state,onEvent)
}

@Composable
fun ReturnContent(
    state: ReturnReceivingContract.State = ReturnReceivingContract.State(),
    onEvent: (ReturnReceivingContract.Event)-> Unit = {}
) {
    val keywordFocusRequester = remember {
        FocusRequester()
    }
    val listState = rememberLazyListState()

    val lastItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
    }

    LaunchedEffect(Unit) {
        keywordFocusRequester.requestFocus()
        onEvent(ReturnReceivingContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ReturnReceivingContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(ReturnReceivingContract.Event.CloseToast)
        },
        onRefresh = {
            onEvent(ReturnReceivingContract.Event.OnRefresh)
        }
    ) {
        Box {
            Column(Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
                TopBar(
                    "Return",
                    titleTag = state.warehouse?.name ?: "",
                    onBack = {
                        onEvent(ReturnReceivingContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(ReturnReceivingContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ReturnReceivingContract.Event.ShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = keywordFocusRequester
                )

                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.list,
                    state = listState,
                    itemContent = {_,it->
                        ReturnItem(
                            it,
                            onClick =  {
                                onEvent(ReturnReceivingContract.Event.OnNavToDetail(it))
                            },
                            onRemove = {
                                onEvent(ReturnReceivingContract.Event.SelectForDelete(it))
                            }
                        )
                    },
                    onReachEnd = {
                        onEvent(ReturnReceivingContract.Event.ReachEnd)
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
                        onEvent(ReturnReceivingContract.Event.ShowAdd(true))
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
                onEvent(ReturnReceivingContract.Event.SelectForDelete(null))
            },
            message = "Are you sure to remove [${state.selectedForDelete.referenceNumber}] from return list?",
            isLoading = state.isDeleting
        ) {
            onEvent(ReturnReceivingContract.Event.ConfirmDelete)
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ReturnReceivingContract.Event.ShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sortItem,
            onSelectSort = {
                onEvent(ReturnReceivingContract.Event.OnSortChange(it))
            }
        )
    }
    AddReturnSheet(state,onEvent)
}


@Composable
fun ReturnItem(
    model: ReturnRow,
    onClick: ()-> Unit,
    onRemove: ()-> Unit,
) {
    MainListItem(
        onClick = onClick,
        typeTitle = model.referenceNumber,
        modelNumber = model.receivingNumber,
        item1 = BaseListItemModel("Customer",model.partnerName?:"",R.drawable.user_square),
        item2 = BaseListItemModel("Customer Code",model.partnerCode?:"",R.drawable.vuesax_linear_user_tag),
        item3 = BaseListItemModel("Receiving Date",model.date?:"",R.drawable.vuesax_linear_calendar_2),
        total = model.receivingDetailCount?.removeZeroDecimal()?:"0",
        totalTitle = "Rows",
        count = "",
        countTitle = "",
        countIcon = null,
        countContent = {
            MyIcon(
                icon = Icons.Default.Clear,
                tint = Primary,
                background = Color.White,
                onClick = onRemove
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReturnSheet(
    state: ReturnReceivingContract.State,
    onEvent: (ReturnReceivingContract.Event)-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (state.showAdd){


        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(ReturnReceivingContract.Event.ShowAdd(false))
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
                    text = "Add Return Receiving",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(12.mdp))
                TitleView(
                    title = "Reference Number"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.referenceNumber,
                    {
                        onEvent(ReturnReceivingContract.Event.ChangeReferenceNumber(it))
                    },
                    leadingIcon = R.drawable.hashtag,
                    hideKeyboard = state.lockKeyboard
                )
                TitleView(
                    title = "Customer"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    TextFieldValue(if (state.customer!=null)"${(state.customer.partnerName).trim().trimIndent()}(${state.customer.partnerCode})" else ""),
                    onValueChange = {
                    },
                    leadingIcon = R.drawable.user_square,
                    readOnly = true,
                    onClick = {
                        onEvent(ReturnReceivingContract.Event.OnShowCustomerList(true))
                    },
                )
                Spacer(Modifier.size(10.mdp))
//                val editDriver = state.selectedDriver == null && state.isDriverIdScanned
                TitleView(title = "Owner")
                InputTextField(
                    TextFieldValue(state.ownerInfo?.let { "${it.ownerName}(${it.ownerCode})" }?:""),
                    {},
                    leadingIcon = R.drawable.vuesax_linear_user_tag,
                    readOnly = true,
                    onClick = {
                        onEvent(ReturnReceivingContract.Event.OnShowOwnerList(true))
                    }
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Receive Date")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.receivingShowDate,
                    {},
                    leadingIcon = R.drawable.vuesax_linear_calendar_2,
                    readOnly = true,
                    onClick = {
                        onEvent(ReturnReceivingContract.Event.ShowDatePicker(true))
                    }
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ReturnReceivingContract.Event.ShowAdd(false))
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
                            onEvent(ReturnReceivingContract.Event.OnAdd)
                        },
                        title = "Save",
                        isLoading = state.isSaving,
                        enabled = state.ownerInfo!=null&&state.customer!=null&&state.receivingDate.isNotEmpty()&&state.referenceNumber.text.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        ListSheet(
            state.showCustomerList,
            title = "Customer List",
            onDismiss = {
                onEvent(ReturnReceivingContract.Event.OnShowCustomerList(false))
            },
            list = state.customerList,
            searchable = true,
            selectedItem = state.customer
        ) {
            onEvent(ReturnReceivingContract.Event.OnSelectCustomer(it))
        }
        ListSheet(
            state.showOwnerList && state.ownerInfoList.size > 1,
            title = "Owner List",
            onDismiss = {
                onEvent(ReturnReceivingContract.Event.OnShowCustomerList(false))
            },
            list = state.ownerInfoList,
            searchable = true,
            selectedItem = state.ownerInfo
        ) {
            onEvent(ReturnReceivingContract.Event.OnSelectOwner(it))
        }
        if (state.showDatePicker){

            DatePickerDialog(
                onDismiss = {
                    onEvent(ReturnReceivingContract.Event.ShowDatePicker(false))
                },
                selectedDate = state.receivingDate,
            ) { f1, f2 ->
                onEvent(ReturnReceivingContract.Event.ChangeReceivingDate(f1,f2))
            }
        }
    }

}