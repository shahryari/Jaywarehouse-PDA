package com.example.jaywarehouse.presentation.rs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.rs.models.PODInvoiceRow
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
import com.example.jaywarehouse.presentation.shipping.PalletBarcode
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import com.example.jaywarehouse.ui.theme.Gray3
import com.example.jaywarehouse.ui.theme.Gray5
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Destination(style = ScreenTransition::class)
@Composable
fun RSScreen(
    navigator: DestinationsNavigator,
    viewModel: RSIntegrationViewModel = koinViewModel()
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it){
                RSIntegrationContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
            }
        }
    }
    RSContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RSContent(
    state: RSIntegrationContract.State = RSIntegrationContract.State(),
    onEvent: (RSIntegrationContract.Event)->Unit = {}
) {
    val searchFocusRequester = remember {
        FocusRequester()
    }


    LaunchedEffect(key1 = Unit) {
        searchFocusRequester.requestFocus()
        onEvent(RSIntegrationContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(RSIntegrationContract.Event.CloseError)
        },
        onRefresh = {
            onEvent(RSIntegrationContract.Event.OnRefresh)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(15.mdp)
            ) {
                TopBar(
                    "RS",
                    onBack = {
                        onEvent(RSIntegrationContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(10.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(RSIntegrationContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(RSIntegrationContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = searchFocusRequester
                )
                Spacer(modifier = Modifier.size(15.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.rsList,
                    itemContent = {_,it->
                        RsItem(it) {
                            onEvent(RSIntegrationContract.Event.OnSelectRs(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(RSIntegrationContract.Event.OnReachEnd)
                    }
                )
            }

        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(RSIntegrationContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.sort,
            onSelectSort = {
                onEvent(RSIntegrationContract.Event.OnSortChange(it))
            }
        )
    }
    UpdateDriverBottomSheet(state,onEvent)
}


@Composable
fun RsItem(
    model: PODInvoiceRow,
    onClick: ()->Unit
) {
    Column(
        Modifier.shadow(1.mdp, RoundedCornerShape(6.mdp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.mdp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(8.mdp)
    ) {
        MyText(
            "#${model.pODInvoiceNumber}",
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        Spacer(Modifier.size(10.mdp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailCard(
                title = "Driver",
                detail = model.driverFullName?:"",
                icon = R.drawable.user_square,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(15.mdp))
            DetailCard(
                title = "Driver Tin",
                detail = model.driverTin?:"",
                icon = R.drawable.note,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.size(10.mdp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailCard(
                title = "Car Number",
                detail = model.carNumber?:"",
                icon = R.drawable.barcode,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.size(15.mdp))
            DetailCard(
                title = "Trailer Number",
                detail = model.trailerNumber?:"",
                icon = R.drawable.vuesax_linear_box,
                modifier = Modifier.weight(1f)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDriverBottomSheet(
    state: RSIntegrationContract.State,
    onEvent: (RSIntegrationContract.Event) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (state.selectedRs!=null){



        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onEvent(RSIntegrationContract.Event.OnSelectRs(null))
            },
            containerColor = Color.White
        ) {
            Column (
                Modifier
                    .padding(horizontal = 24.mdp)
                    .padding(bottom = 24.mdp)
            ){
                Row {
                    MyText(
                        text = "Update Driver [",
                        fontSize = 16.sp,
                        color = Color(0xFF767676)
                    )
                    MyText(
                        text = state.selectedRs.pODInvoiceNumber?:"",
                        fontSize = 16.sp,
                    )
                    MyText(
                        text = "]",
                        fontSize = 16.sp,
                        color = Color(0xFF767676)
                    )
                }

                Spacer(Modifier.size(44.mdp))
                InputTextField(
                    state.driverTin,
                    onValueChange = {
                        onEvent(RSIntegrationContract.Event.OnDriverTinChange(it))
                    },
                    onAny = {
                        onEvent(RSIntegrationContract.Event.OnScanDriverTin)
                    },
                    leadingIcon = R.drawable.note,
//                    hideKeyboard = state.lockKeyboard,
                    trailingIcon = R.drawable.tick,
                    label = "Driver Tin",
                    onTrailingClick = {
                        onEvent(RSIntegrationContract.Event.OnScanDriverTin)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(8.mdp))
                val editDriver = state.selectedDriver == null && state.isDriverScanned
                InputTextField(
                    state.driver,
                    onValueChange = {
                        onEvent(RSIntegrationContract.Event.OnDriverChange(it))
                    },
                    onAny = {

                    },
                    leadingIcon = R.drawable.user_square,
//                    hideKeyboard = state.lockKeyboard,
                    label = "Driver",
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(8.mdp))
                InputTextField(
                    state.carNumber,
                    onValueChange = {
                        onEvent(RSIntegrationContract.Event.OnCarNumberChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.vuesax_linear_box,
//                    hideKeyboard = state.lockKeyboard,
                    label = "Car Number",
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.size(8.mdp))
                InputTextField(
                    state.trailer,
                    onValueChange = {
                        onEvent(RSIntegrationContract.Event.OnTrailerChange(it))
                    },
                    onAny = {},
                    leadingIcon = R.drawable.vuesax_linear_box,
                    label = "Trailer Number",
//                    hideKeyboard = state.lockKeyboard,
                    enabled = editDriver,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(Modifier.size(30.mdp))
                Row(Modifier.fillMaxWidth()) {
                    MyButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onEvent(RSIntegrationContract.Event.OnSelectRs(null))
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
                            onEvent(RSIntegrationContract.Event.OnSubmit(state.selectedRs))
                        },
                        title = "Submit",
                        isLoading = state.isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}




@Preview
@Composable
private fun RSPreview() {
    RSContent()
}