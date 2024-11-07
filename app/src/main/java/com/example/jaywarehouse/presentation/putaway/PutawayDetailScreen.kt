package com.example.jaywarehouse.presentation.putaway

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.data.putaway.model.PutawayListRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.BasicDialog
import com.example.jaywarehouse.presentation.common.composables.DetailCard
import com.example.jaywarehouse.presentation.common.composables.DetailItem
import com.example.jaywarehouse.presentation.common.composables.InputTextField
import com.example.jaywarehouse.presentation.common.composables.MyButton
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.MyText
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TitleView
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.MainGraph
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.DashboardScreenDestination
import com.example.jaywarehouse.presentation.destinations.PutawayScreenDestination
import com.example.jaywarehouse.presentation.putaway.contracts.PutawayDetailContract
import com.example.jaywarehouse.presentation.putaway.viewmodels.PutawayDetailViewModel
import com.example.jaywarehouse.ui.theme.Black
import com.example.jaywarehouse.ui.theme.Gray2
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.example.jaywarehouse.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination(style = ScreenTransition::class)
@Composable
fun PutawayDetailScreen(
    navigator: DestinationsNavigator,
    putRow: PutawayListGroupedRow,
    viewModel: PutawayDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(putRow)
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
                PutawayDetailContract.Effect.NavBack -> navigator.popBackStack()
                PutawayDetailContract.Effect.NavToDashboard -> {
                    navigator.navigate(DashboardScreenDestination){
                        popUpTo(PutawayScreenDestination){
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
    PutawayDetailContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PutawayDetailContent(
    state: PutawayDetailContract.State = PutawayDetailContract.State(),
    onEvent: (PutawayDetailContract.Event)->Unit = {}
) {
    val focusRequester = FocusRequester()

    val refreshState = rememberPullRefreshState(
        refreshing = state.loadingState == Loading.REFRESHING,
        onRefresh = { onEvent(PutawayDetailContract.Event.OnRefresh) }
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(PutawayDetailContract.Event.CloseError)
        },
        toast = state.toast,
        onHideToast = {
            onEvent(PutawayDetailContract.Event.HideToast)
        }
    ) {

        Box(
            Modifier
                .fillMaxSize()) {
            Column(
                Modifier
                    .pullRefresh(refreshState)
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    title = "Puaway",
                    onBack = {
                        onEvent(PutawayDetailContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    value = state.keyword,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeKeyword(it))
                    },
                    onSearch = {
                        onEvent(PutawayDetailContract.Event.OnSearch)
                    },
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(PutawayDetailContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.putaways){
                        PutawayDetailItem(it){
                            onEvent(PutawayDetailContract.Event.OnSelectPut(it))
                        }
                        Spacer(modifier = Modifier.size(7.mdp))
                    }
                    item {
                        onEvent(PutawayDetailContract.Event.OnReachEnd)
                    }
                    item {
                        Spacer(modifier = Modifier.size(70.mdp))
                    }
                }
            }
            PullRefreshIndicator(refreshing = state.loadingState == Loading.REFRESHING, state = refreshState, modifier = Modifier.align(
                Alignment.TopCenter) )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(PutawayDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(PutawayDetailContract.Event.OnSortChange(it))
            }
        )
    }
    PutawayBottomSheet(state,onEvent)
}

@Composable
fun MyAlertDialog(
    onDismiss: ()->Unit,
    message: String = "Your put operation is completed successfully",
) {
    BasicDialog(
        onDismiss = onDismiss,
        positiveButton = "Ok",
        onPositiveClick = {
            onDismiss()
        },
    ) {
        Icon(
            painterResource(id = R.drawable.broken___essentional__ui___danger_triang),
            contentDescription = "",
            tint = Orange,
            modifier = Modifier.size(80.mdp)
        )
        Spacer(Modifier.size(10.mdp))
        MyText(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
fun RegisteredItem(i: Int, date: String , time: String,onRemove:()->Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Gray2)
            .padding(top = 4.mdp, bottom = 4.mdp, start = 2.mdp, end = 15.mdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier
            .size(40.mdp)
            .clip(CircleShape)
            .background(Black),
            contentAlignment = Alignment.Center
        ){
            MyText(
                i.toString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }

        MyText(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
        MyText(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
        Spacer(modifier = Modifier.size(20.mdp))
        Box(
            Modifier
                .clip(RoundedCornerShape(4.mdp))
                .background(Color.Gray)
                .clickable {
                    onRemove()
                }
                .padding(5.mdp)
        ) {

            Icon(
                Icons.Default.Clear,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(12.mdp)
            )
        }
    }
}


@Composable
fun PutawayDetailItem(
    model: PutawayListRow,
    onClick: ()->Unit
) {
    BaseListItem(
        onClick = onClick,
        item1 = BaseListItemModel("Name",model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode,R.drawable.barcode),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber,R.drawable.note),
        item4 = BaseListItemModel("Batch Number",model.batchNumber?:"",R.drawable.vuesax_linear_box),
        item5 = BaseListItemModel("Expiration Date",model.expireDateString?:"",R.drawable.calendar_add),
        quantity = model.warehouseLocationCode,
        quantityTitle = "Location",
        scan = model.quantity.toString(),
        scanTitle = "Quantity"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutawayBottomSheet(
    state: PutawayDetailContract.State,
    onEvent: (PutawayDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedPutaway!=null){

        val locationFocusRequester = remember {
            FocusRequester()
        }
        val barcodeFocusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(Unit) {
            locationFocusRequester.requestFocus()
        }
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(PutawayDetailContract.Event.OnSelectPut(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                MyText(
                    text = "Putaway",
                    fontWeight = FontWeight.W500,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(10.mdp))

                DetailCard(
                    title = "Name",
                    icon = R.drawable.vuesax_outline_3d_cube_scan,
                    detail = state.selectedPutaway.productName,
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.note,
                        detail = state.selectedPutaway.productCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.selectedPutaway.productBarcodeNumber,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                if(state.selectedPutaway.batchNumber!=null || state.selectedPutaway.expireDateString!=null)Row(Modifier.fillMaxWidth()) {
                    if(state.selectedPutaway.batchNumber!=null)DetailCard(
                        title = "Batch Number",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedPutaway.batchNumber,
                        modifier = Modifier.weight(1f)
                    )
                    if(state.selectedPutaway.batchNumber!=null && state.selectedPutaway.expireDateString!=null)Spacer(Modifier.size(5.mdp))
                    if(state.selectedPutaway.expireDateString != null)DetailCard(
                        title = "Expiration Date",
                        icon = R.drawable.calendar_add,
                        detail = state.selectedPutaway.expireDateString,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Location",
                        icon = R.drawable.location,
                        detail = state.selectedPutaway.warehouseLocationCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedPutaway.quantity.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Location Code"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.location,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeLocation(it))
                    },
                    onAny = {
                        barcodeFocusRequester.requestFocus()
                    },
                    leadingIcon = R.drawable.location,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = locationFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(10.mdp))
                TitleView(title = "Barcode")
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.barcode,
                    onValueChange = {
                        onEvent(PutawayDetailContract.Event.OnChangeBarcode(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.barcode,
//                    hideKeyboard = state.lockKeyboard,
                    focusRequester = barcodeFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(PutawayDetailContract.Event.OnSelectPut(null))
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

                            onEvent(PutawayDetailContract.Event.OnSavePutaway(state.selectedPutaway))
                        },
                        title = "Save",
                        isLoading = state.onSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PutawayDetailPreview() {
    PutawayDetailContent(
        state = PutawayDetailContract.State(
            selectedPutaway = PutawayListRow(
                batchNumber = "test",
                expireDateString = "today",
                ownerFullName = "mohyeddin",
                productBarcodeNumber = "e342432",
                productCode = "dkfsodif",
                32002,
                productName = "osdkod",
                30220,
                20,
                32002,
                20202,
                "A-01-01"
            )
        )
    )

}