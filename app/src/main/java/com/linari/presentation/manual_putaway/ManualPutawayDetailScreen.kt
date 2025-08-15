package com.linari.presentation.manual_putaway

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linari.R
import com.linari.data.common.utils.mdp
import com.linari.data.common.utils.removeZeroDecimal
import com.linari.data.manual_putaway.models.ManualPutawayDetailRow
import com.linari.data.manual_putaway.models.ManualPutawayRow
import com.linari.presentation.common.composables.DetailCard
import com.linari.presentation.common.composables.DetailItem
import com.linari.presentation.common.composables.InputTextField
import com.linari.presentation.common.composables.MyButton
import com.linari.presentation.common.composables.MyIcon
import com.linari.presentation.common.composables.MyLazyColumn
import com.linari.presentation.common.composables.MyScaffold
import com.linari.presentation.common.composables.MyText
import com.linari.presentation.common.composables.SortBottomSheet
import com.linari.presentation.common.composables.TitleView
import com.linari.presentation.common.composables.TopBar
import com.linari.presentation.common.utils.SIDE_EFFECT_KEY
import com.linari.presentation.common.utils.ScreenTransition
import com.linari.presentation.counting.ConfirmDialog
import com.linari.presentation.manual_putaway.contracts.ManualPutawayDetailContract
import com.linari.presentation.manual_putaway.viewmodels.ManualPutawayDetailViewModel
import com.linari.ui.theme.Background
import com.linari.ui.theme.Gray3
import com.linari.ui.theme.Gray5
import com.linari.ui.theme.Orange
import com.linari.ui.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun ManualPutawayDetailScreen(
    navigator: DestinationsNavigator,
    putaway: ManualPutawayRow,
    viewModel: ManualPutawayDetailViewModel = koinViewModel(
        parameters = {
            parametersOf(putaway)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when (it) {
                ManualPutawayDetailContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }

    ManualPutawayDetailContent(state, onEvent)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ManualPutawayDetailContent(
    state: ManualPutawayDetailContract.State = ManualPutawayDetailContract.State(),
    onEvent: (ManualPutawayDetailContract.Event) -> Unit
) {
    MyScaffold(
        error = state.error,
        onCloseError = {
            onEvent(ManualPutawayDetailContract.Event.OnCloseError)
        },
        toast = state.toast,
        loadingState = state.loadingState,
        onHideToast = {
            onEvent(ManualPutawayDetailContract.Event.HideToast)
        },
        onRefresh = {
            onEvent(ManualPutawayDetailContract.Event.OnRefresh)
        }
    ) {
        state.putaway
        Column(
            Modifier
                .fillMaxSize()
                .padding(15.mdp)) {
            TopBar(
                title = "Putaway",
                titleTag = state.warehouse?.name ?: "",
                onBack = {
                    onEvent(ManualPutawayDetailContract.Event.OnNavBack)
                },
                endIcon = R.drawable.tick,
                endIconEnabled = state.putaway?.let { it.total == it.quantity } ?: false,
                onEndClick = {
                    onEvent(ManualPutawayDetailContract.Event.OnShowConfirmFinish(true))
                }
            )
            Spacer(Modifier.size(20.mdp))
            MyLazyColumn(
                items = state.details,
                itemContent = {i,it->
                    if (AssignedFrom.getFromValue(state.putaway?.createdBy) == AssignedFrom.Form){
                        PutawayDetailItem(state.details.size-i,it, isWeight = state.putaway?.isWeight == true) {
//                            onEvent(ManualPutawayDetailContract.Event.OnSelectDetails(it))
                        }
                    } else {

                        ManualPutawayDetailItem(state.details.size-i,it,selected = it == state.selectedForRemove, isWeight = state.putaway?.isWeight == true){
                            onEvent(ManualPutawayDetailContract.Event.OnSelectDetailForRemove(it))
                        }
                    }
                },
                onReachEnd = {
//                    onEvent(ManualPutawayDetailContract.Event.OnReachEnd)
                },
                spacerSize = 7.mdp,
                header = {
                    Column(
                        Modifier.background(Background)
                    ) {
                        if (state.putaway!=null){
                            ManualPutawayItem(state.putaway, expandable = false){}
                        }
                        if (AssignedFrom.getFromValue(state.putaway?.createdBy) != AssignedFrom.Form){
                            Spacer(Modifier.size(10.mdp))
                            Row(Modifier.fillMaxWidth()) {
                                InputTextField(
                                    state.quantity,
                                    onValueChange = {
                                        onEvent(ManualPutawayDetailContract.Event.OnQuantityChange(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    leadingIcon = R.drawable.box_search,
                                    decimalInput = state.putaway?.isWeight == true,
                                    required = true,
                                    suffix = if (state.putaway?.isWeight == true)"kg" else "",
                                    label = if (state.putaway?.isWeight == true) "Weight" else "Quantity",
                                )
                            }
                            Spacer(Modifier.size(10.mdp))
                            InputTextField(
                                state.locationCode,
                                onValueChange = {
                                    onEvent(ManualPutawayDetailContract.Event.OnLocationCodeChange(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                onAny = {
                                    onEvent(ManualPutawayDetailContract.Event.OnAddClick)
                                },
                                leadingIcon = R.drawable.location,
                                required = true,

                                hideKeyboard = state.lockKeyboard,
                                label = "Location Code",
                            )
                            Spacer(Modifier.size(10.mdp))

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.mdp))
                                    .background(Color.White)
                                    .background(Primary.copy(0.2f))
                                    .clickable(!state.isScanning) {
                                        onEvent(ManualPutawayDetailContract.Event.OnAddClick)
                                    }
                                    .border(1.mdp, Primary, RoundedCornerShape(6.mdp))
                                    .padding(9.mdp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (state.isScanning) CircularProgressIndicator(
                                    modifier = Modifier.size(24.mdp)
                                ) else {
                                    MyIcon(
                                        icon = Icons.Default.Add, showBorder = false,
                                        background = Color.Transparent,
                                        tint = Primary,
                                        clickable = false,
                                    )
                                }
                            }
                            Spacer(Modifier.size(20.mdp))

                        }
                        else MyText(
                            "This putaway can only change in panel.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 10.mdp, bottom = 10.mdp).align(Alignment.CenterHorizontally),
                            color = Color.Black.copy(0.7f)
                        )
                    }
                }
            )
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ManualPutawayDetailContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.selectedSort,
            onSelectSort = {
                onEvent(ManualPutawayDetailContract.Event.OnSortChange(it))
            }
        )
    }
    if (state.selectedForRemove!=null){
        ConfirmDialog(
            isLoading = state.isDeleting,
            onDismiss = {
                onEvent(ManualPutawayDetailContract.Event.OnSelectDetailForRemove(null))
            },
            onConfirm = {
                onEvent(ManualPutawayDetailContract.Event.OnRemove(state.selectedForRemove))
            }
        )
    }
    if(state.showConfirmFinish){
        ConfirmDialog(
            onDismiss = {
                onEvent(ManualPutawayDetailContract.Event.OnShowConfirmFinish(false))
            },
            onConfirm = {
                onEvent(ManualPutawayDetailContract.Event.OnSubmit)
            },
            message = "Are you sure to finish this putaway?",
            isLoading = state.isFinishing,
            tint = Orange,
            title = "Confirm"
        )
    }
    PutawayBottomSheet(state,onEvent)
}


@Composable
fun ManualPutawayDetailItem(
    i: Int,
    detail: ManualPutawayDetailRow,
    selected: Boolean = false,
    isWeight: Boolean,
    onRemove: ()->Unit
) {
    DetailItem(
        i,
        first = detail.quantity.removeZeroDecimal().toString() + if (isWeight) " kg" else "",
        firstIcon = R.drawable.box_search,
        second = detail.locationCode,
        secondIcon = R.drawable.location,
        selected = selected,
        onRemove = onRemove
    )
}


@Composable
fun PutawayDetailItem(
    i: Int,
    detail: ManualPutawayDetailRow,
    selected: Boolean = false,
    isWeight: Boolean,
    onRemove: ()->Unit = {},
    onClick: ()->Unit
) {
    DetailItem(
        i,
        first = detail.quantity.removeZeroDecimal().toString() + if(isWeight) " kg" else "",
        firstIcon = R.drawable.box_search,
        second = detail.locationCode,
        secondIcon = R.drawable.location,
        selected = selected,
        removable = false,
        onClick = onClick,
        onRemove = onRemove
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutawayBottomSheet(
    state: ManualPutawayDetailContract.State,
    onEvent: (ManualPutawayDetailContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedDetail!=null){

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
                onEvent(ManualPutawayDetailContract.Event.OnSelectDetails(null))
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
                    detail = state.putaway?.productName?:"",
                )
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Product Code",
                        icon = R.drawable.keyboard2,
                        detail = state.putaway?.productCode?:"",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Barcode",
                        icon = R.drawable.barcode,
                        detail = state.putaway?.productBarcodeNumber?:"",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                if(state.putaway?.batchNumber!=null || state.putaway?.expireDate!=null)Row(Modifier.fillMaxWidth()) {
                    if(state.putaway.batchNumber!=null)DetailCard(
                        title = "Batch No.",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.putaway.batchNumber,
                        modifier = Modifier.weight(1f)
                    )
                    if(state.putaway.batchNumber!=null && state.putaway.expireDate!=null)Spacer(Modifier.size(5.mdp))
                    if(state.putaway.expireDate != null)DetailCard(
                        title = "Exp Date",
                        icon = R.drawable.calendar_add,
                        detail = state.putaway.expireDate,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                Row(Modifier.fillMaxWidth()) {
                    DetailCard(
                        title = "Location",
                        icon = R.drawable.location,
                        detail = state.selectedDetail.locationCode,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.size(5.mdp))
                    DetailCard(
                        title = "Quantity",
                        icon = R.drawable.vuesax_linear_box,
                        detail = state.selectedDetail.quantity.removeZeroDecimal().toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.size(10.mdp))
                TitleView(
                    title = "Location Code"
                )
                Spacer(Modifier.size(5.mdp))
                InputTextField(
                    state.locationCode,
                    onValueChange = {
                        onEvent(ManualPutawayDetailContract.Event.OnLocationCodeChange(it))
                    },
                    onAny = {
                        barcodeFocusRequester.requestFocus()
                    },
                    leadingIcon = R.drawable.location,
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = locationFocusRequester,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(15.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(ManualPutawayDetailContract.Event.OnSelectDetails(null))
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

                            onEvent(ManualPutawayDetailContract.Event.OnCheckLocation(state.selectedDetail,state.locationCode.text))
                        },
                        title = "Done",
                        isLoading = state.isScanning,
                        enabled = state.locationCode.text.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ManualPutawayDetailPreview() {
    ManualPutawayDetailContent(
        onEvent = {}
    )
}


