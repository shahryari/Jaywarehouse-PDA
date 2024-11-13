package com.example.jaywarehouse.presentation.packing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.packing.model.PackingRow
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
import com.example.jaywarehouse.presentation.common.composables.MyIcon
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.SuccessToast
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.counting.ConfirmDialog
import com.example.jaywarehouse.presentation.destinations.PackingDetailScreenDestination
import com.example.jaywarehouse.presentation.packing.contracts.PackingContract
import com.example.jaywarehouse.presentation.packing.viewmodels.PackingViewModel
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun PackingScreen(
    navigator: DestinationsNavigator,
    viewModel: PackingViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is PackingContract.Effect.NavigateToPackingDetail -> {
                    navigator.navigate(PackingDetailScreenDestination(it.packingRow))
                }
            }
        }
    }
    PackingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackingContent(
    state: PackingContract.State,
    onEvent: (PackingContract.Event) -> Unit
) {
    val sortList = mapOf("Model" to "Model", "Barcode" to "Barcode","Created On" to "CreatedOn","Location" to "Location")
    val searchFocusRequester = FocusRequester()
    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = {
            onEvent(PackingContract.Event.OnRefresh)
        })
    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(PackingContract.Event.FetchData)
    }
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(PackingContract.Event.HideToast)
        }
    }
    MyScaffold(
        offset = (-70).mdp,
        loadingState = state.loadingState
    ) {

        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {

                MyText(
                    text = stringResource(id = R.string.packing),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(PackingContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(PackingContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PackingContract.Event.OnShowFilterList(true))
                    },
                    hideKeyboard = false,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.packingModel?.rows?: emptyList()){
                        PackedItem(packingRow = it,
                            onClick = {
                                onEvent(PackingContract.Event.OnPackingClick(it))
                            },
                            onRemove = {
                                onEvent(PackingContract.Event.OnSelectPack(it.packingID))
                            },
                            showDeleteIcon = true
                        )
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(PackingContract.Event.OnReachedEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(80.mdp))
                    }
                }
            }
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(Modifier.align(Alignment.Center))
            SuccessToast(message = state.toast)
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

            FloatingActionButton(
                onClick = {
                    onEvent(PackingContract.Event.OnShowAddDialog(true))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 85.mdp, end = 15.mdp),
                containerColor = Orange,
                shape = CircleShape,
            ) {
                Box(
                    modifier = Modifier
                        .padding(15.mdp)
                        .clip(RoundedCornerShape(7.mdp))
                        .border(
                            2.mdp, Color.White,
                            RoundedCornerShape(7.mdp)
                        )
                        .padding(3.mdp)
                ){
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(22.mdp)
                    )
                }
            }
        }
    }
    if (state.error.isNotEmpty()){
        ErrorDialog(
            onDismiss = {
                onEvent(PackingContract.Event.OnClearError)
            },
            message = state.error
        )
    }
    if (state.showFilterList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PackingContract.Event.OnShowFilterList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PackingContract.Event.OnSortChange(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(PackingContract.Event.OnOrderChange(it))
            }
        )
    }
    if (state.selectedPack!=null){
        ConfirmDialog(onDismiss = {
            onEvent(PackingContract.Event.OnSelectPack(null))
        }) {
            onEvent(PackingContract.Event.OnRemoveClick(state.selectedPack))
        }
    }
    if (state.showAddDialog){
        AddDialog(state, onEvent)
    }
}

@Composable
fun AddDialog(
    state: PackingContract.State,
    onEvent: (PackingContract.Event) -> Unit
) {
    BasicDialog(
        onDismiss = {
            onEvent(PackingContract.Event.OnShowAddDialog(false))
        },
        positiveButton = "Save",
        negativeButton = "Cancel",
        onPositiveClick = {
            onEvent(PackingContract.Event.OnAddClick)
        },
        showCloseIcon = true,
        title = "Create A New Pack",
        isLoading = state.isPacking,
        onNegativeClick = {
            onEvent(PackingContract.Event.OnShowAddDialog(false))
        }
    ) {
        Spacer(modifier = Modifier.size(20.mdp))
        MyText(
            text = "Packing Number" ,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start),
            color = Color.White
        )
        Spacer(modifier = Modifier.size(10.mdp))
        DialogInput(
            value = state.packingNumber,
            onValueChange = {
                onEvent(PackingContract.Event.OnPackingNumberChange(it))
            },
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.size(20.mdp))
        MyText(
            text = "Customer" ,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start),
            color = Color.White
        )
        Spacer(modifier = Modifier.size(15.mdp))
        AutoDropDownTextField(
            value = state.customerName,
            onValueChange = {
                onEvent(PackingContract.Event.OnCustomerNameChange(it))
            } ,
            showSuggestion = state.customerName.text.length>1,
            suggestions = state.customerList?: emptyList() ,
            onSuggestionClick = {
                onEvent(PackingContract.Event.OnCustomerChange(it))
            }
        )
//        Column() {
//            Row(
//                Modifier
//                    .shadow(2.mdp, RoundedCornerShape(10.mdp))
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(10.mdp))
//                    .background(Color.White.copy(0.2f))
//                    .clickable { onEvent(PackingContract.Event.OnShowPopup(true)) }
//                    .padding(vertical = 7.mdp, horizontal = 5.mdp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Box(modifier = Modifier.padding(7.mdp)) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.vuesax_linear_user),
//                            contentDescription = "",
//                            tint = Color.White,
//                            modifier = Modifier
//                                .size(24.mdp)
//                        )
//                    }
//                    MyText(text = state.selectedCustomer?.customerName?:"",color = Color.White)
//                }
//                Box(modifier = Modifier.padding(7.mdp)) {
//                    Icon(
//                        Icons.Default.ArrowDropDown,
//                        contentDescription = "",
//                        tint = Color.White,
//                        modifier = Modifier
//                            .size(24.mdp)
//                    )
//                }
//            }
//            if (state.showPopup)Popup(
//                alignment = Alignment.BottomCenter,
//                offset = IntOffset(0,40),
//                onDismissRequest = {onEvent(PackingContract.Event.OnShowPopup(false))}
//            ) {
//                LazyColumn(
//                    Modifier
//                        .fillMaxWidth(0.7f)
//                        .clip(RoundedCornerShape(10.mdp))
//                        .background(Color.Gray)) {
//                    items(state.customerList?: emptyList()){
//                        Row(
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(8.mdp)
//                                .clickable {
//                                    onEvent(PackingContract.Event.OnCustomerChange(it))
//                                }
//                        ) {
//                            MyText(
//                                text = it.customerName,
//                                fontWeight = FontWeight.Normal,
//                                color = Color.White
//                            )
//                        }
//                        HorizontalDivider()
//                    }
//                }
//            }
//        }
        Spacer(modifier = Modifier.size(20.mdp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.hashtag,
    onAny: ()->Unit = {},
    focusRequester: FocusRequester = FocusRequester(),
    hideKeyboard: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val isKeyboardOpen = WindowInsets.isImeVisible


//    (LocalContext.current as Activity).window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    LaunchedEffect(key1 = isFocused,isKeyboardOpen) {
        if ((isFocused || isKeyboardOpen) && hideKeyboard){
            keyboard?.hide()
        }
    }
    SideEffect {
        if ((isFocused || isKeyboardOpen) && hideKeyboard){
            keyboard?.hide()
        }
    }
    BasicTextField(
        value,
        onValueChange = {
            if (it.text.endsWith('\n') || (it.text.endsWith('\r'))){
                onAny()
            } else {
                onValueChange(it)
            }
        },
        modifier = modifier
            .focusRequester(focusRequester = focusRequester)
            .onFocusChanged {
                if (it.isFocused && hideKeyboard) {
                    keyboard?.hide()
                }
                isFocused = it.isFocused
            }
            .onKeyEvent {
                if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                    onAny()
                    true
                } else {
                    false
                }
            },
        maxLines = 1,
        textStyle = TextStyle.Default.copy(color = Color.White),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        decorationBox = {
            Row(
                Modifier
                    .shadow(2.mdp, RoundedCornerShape(10.mdp))
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.mdp))
                    .background(Color.White.copy(0.2f))
                    .padding(vertical = 7.mdp, horizontal = 5.mdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.padding(7.mdp)) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.mdp)
                        )
                    }
                    Box(modifier = Modifier){
                        it()
                        if (isFocused && hideKeyboard)Box(modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize()
                            .clip(
                                RoundedCornerShape(10.mdp)
                            )
                            .clickable { }
                        )
                    }


                }
                if (value.text.isNotEmpty()) MyIcon(icon = R.drawable.vuesax_bulk_broom) {
                    onValueChange(TextFieldValue())
                }
            }
        }
    )
}

@Composable
fun PackedItem(
    packingRow: PackingRow,
    showDeleteIcon: Boolean,
    onClick: ()->Unit,
    enableShowDetail: Boolean = false,
    onRemove: ()->Unit
) {
    BaseListItem(
        onClick = onClick,
        onRemove = onRemove,
        item2 = BaseListItemModel("Packing Number",packingRow.packingNumber, R.drawable.hashtag,MaterialTheme.typography.bodyMedium),
        item3 = BaseListItemModel("Created On",packingRow.date, R.drawable.vuesax_linear_calendar_add,MaterialTheme.typography.bodySmall),
        item4 = BaseListItemModel("Customer",packingRow.customerName, R.drawable.vuesax_linear_user,MaterialTheme.typography.bodyMedium),
        quantity = packingRow.itemCount,
        showDeleteButton = showDeleteIcon,
        enableShowDetail = enableShowDetail,
        quantityTitle = "Items",
        scanTitle = "Quantity",
        scan = packingRow.sumPackedQty?:0
    )
}

@Preview
@Composable
private fun PackingPreview() {
    MyScaffold {
        PackingContent(PackingContract.State(), onEvent = {})
    }
}