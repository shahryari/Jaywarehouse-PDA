package com.example.jaywarehouse.presentation.shipping

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.composables.AutoDropDownTextField
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.ErrorDialog
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
import com.example.jaywarehouse.presentation.destinations.ShippingDetailScreenDestination
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import com.example.jaywarehouse.presentation.shipping.viewmodels.ShippingViewModel
import com.example.jaywarehouse.ui.theme.Orange
import com.example.jaywarehouse.ui.theme.poppins
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@MainGraph
@Destination(style = ScreenTransition::class)
@Composable
fun ShippingScreen(
    navigator: DestinationsNavigator,
    viewModel: ShippingViewModel = koinViewModel()
) {

    val state = viewModel.state
    val onEvent = viewModel::onEvent

    LaunchedEffect(key1 = SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                is ShippingContract.Effect.NavigateToShippingDetail -> navigator.navigate(ShippingDetailScreenDestination(it.shippingRow))
            }
        }
    }
    ShippingContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShippingContent(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    val sortList = mapOf("Model" to "Model", "Barcode" to "Barcode","Created On" to "CreatedOn","Location" to "Location")
    val searchFocusRequester = FocusRequester()
    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(ShippingContract.Event.FetchData)

    }
    val refreshState = rememberPullRefreshState(refreshing = state.loadingState == Loading.REFRESHING, onRefresh = {
        onEvent(ShippingContract.Event.OnRefresh)
    })
    LaunchedEffect(key1 = state.toast) {
        if(state.toast.isNotEmpty()){
            delay(3000)
            onEvent(ShippingContract.Event.HideToast)
        }
    }
    MyScaffold(offset = (-70).mdp, loadingState = state.loadingState) {

        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .padding(15.mdp)
            ) {

                MyText(
                    text = stringResource(id = R.string.shipping),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    value = state.keyword,
                    {
                        onEvent(ShippingContract.Event.OnKeywordChange(it))
                    },
                    onSearch = {
                        onEvent(ShippingContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ShippingContract.Event.OnShowFilterList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                LazyColumn (Modifier.fillMaxSize()){
                    items(state.shippingList){
                        ShippingItem(row = it, onClick = {
                            onEvent(ShippingContract.Event.OnShippingClick(it))
                        }, onRemove = {
                            onEvent(ShippingContract.Event.OnSelectShip(it.shippingID))
                        })
                        Spacer(modifier = Modifier.size(10.mdp))
                    }
                    item {
                        onEvent(ShippingContract.Event.OnReachEnd)
                    }
                    item{
                        Spacer(modifier = Modifier.size(80.mdp))
                    }
                }
            }
//            if (state.loadingState == Loading.LOADING) CircularProgressIndicator(Modifier.align(Alignment.Center))
            SuccessToast(message = state.toast)
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(Alignment.TopCenter) )

            FloatingActionButton(
                onClick = {
                    onEvent(ShippingContract.Event.OnShowAddDialog(true))
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
                onEvent(ShippingContract.Event.OnClearError)
            },
            message = state.error
        )
    }
    if (state.showFilterList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ShippingContract.Event.OnShowFilterList(false))
            },
            sortOptions = sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(ShippingContract.Event.OnSortChange(it))
            },
            selectedOrder = state.order,
            onSelectOrder = {
                onEvent(ShippingContract.Event.OnOrderChange(it))
            }
        )
    }
    if (state.selectedShip!=null){
        ConfirmDialog(onDismiss = {
            onEvent(ShippingContract.Event.OnSelectShip(null))
        }) {
            onEvent(ShippingContract.Event.OnRemoveClick(state.selectedShip))
        }
    }
    if (state.showAddDialog){
        AddDialog(state, onEvent)
    }
}

@Composable
fun ShippingItem(
    row: ShippingRow,
    onClick: () -> Unit,
    onRemove: ()->Unit,
    enableShowDetail: Boolean = false,
    enableRemove: Boolean = true,
    showAll: Boolean = true
) {
    BaseListItem(
        onClick = {
            onClick()
        },
        onRemove = onRemove,
        item2 = if (showAll)BaseListItemModel("Shipping Number",row.shippingNumber, R.drawable.hashtag,MaterialTheme.typography.bodyMedium) else null,
        item3 = if (showAll)BaseListItemModel("Created On",row.date, R.drawable.vuesax_linear_calendar_add,MaterialTheme.typography.bodyMedium) else null,
        item4 = BaseListItemModel("Driver",row.driverName?:"", R.drawable.vuesax_linear_user_tag,MaterialTheme.typography.bodyMedium),
        quantity = row.scanCount?:0,
        showDeleteButton = enableRemove,
        enableShowDetail = enableShowDetail,
        quantityTitle = "Packs",
        scanTitle = "SumQty",
        scan = row.itemCount?:0
    )
}

@Composable
fun AddDialog(
    state: ShippingContract.State,
    onEvent: (ShippingContract.Event) -> Unit
) {
    BasicDialog(
        onDismiss = {
            onEvent(ShippingContract.Event.OnShowAddDialog(false))
        },
        positiveButton = "Save",
        negativeButton = "Cancel",
        onPositiveClick = {
            onEvent(ShippingContract.Event.OnAddClick)
        },
        showCloseIcon = true,
        title = "Create A New Ship",
        isLoading = state.isShipping,
        onNegativeClick = {
            onEvent(ShippingContract.Event.OnShowAddDialog(false))
        }
    ) {
        Spacer(modifier = Modifier.size(20.mdp))
        
        Spacer(modifier = Modifier.size(20.mdp))
        MyText(
            text = "Driver" ,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start),
            color = Color.White
        )
        Spacer(modifier = Modifier.size(15.mdp))
        AutoDropDownTextField(
            state.driverName,
            onValueChange = {
                onEvent(ShippingContract.Event.OnDriverNameChange(it))
            },
            suggestions = state.driverList?: emptyList(),
            showSuggestion = state.driverName.text.length > 1,
            onSuggestionClick = {
                onEvent(ShippingContract.Event.OnDriverChange(it))
            }
        )
//        Column {
//            Row(
//                Modifier
//                    .shadow(2.mdp, RoundedCornerShape(10.mdp))
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(10.mdp))
//                    .background(Color.White.copy(0.2f))
//                    .clickable { onEvent(ShippingContract.Event.OnShowPopup(true)) }
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
//                    MyText(text = state.selectedDriver?.driverName?:"",color = Color.White)
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
//            if (state.showPopup) Popup(
//                alignment = Alignment.BottomCenter,
//                offset = IntOffset(0,40),
//                onDismissRequest = {onEvent(ShippingContract.Event.OnShowPopup(false))}
//            ) {
//                LazyColumn(
//                    Modifier
//                        .fillMaxWidth(0.7f)
//                        .clip(RoundedCornerShape(10.mdp))
//                        .background(Color.Gray)) {
//                    items(state.driverList?: emptyList()){
//                        Row(
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(8.mdp)
//                                .clickable {
//                                    onEvent(ShippingContract.Event.OnDriverChange(it))
//                                }
//                        ) {
//                            MyText(
//                                text = it.driverName,
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


@Preview
@Composable
private fun ShippingPreview() {
    MyScaffold {
        ShippingContent(ShippingContract.State(),{})
    }
}