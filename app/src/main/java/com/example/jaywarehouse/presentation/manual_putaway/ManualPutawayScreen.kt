package com.example.jaywarehouse.presentation.manual_putaway

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.example.jaywarehouse.R
import com.example.jaywarehouse.data.common.utils.mdp
import com.example.jaywarehouse.data.common.utils.removeZeroDecimal
import com.example.jaywarehouse.data.manual_putaway.models.ManualPutawayRow
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.presentation.common.composables.BaseListItem
import com.example.jaywarehouse.presentation.common.composables.BaseListItemModel
import com.example.jaywarehouse.presentation.common.composables.MyLazyColumn
import com.example.jaywarehouse.presentation.common.composables.MyScaffold
import com.example.jaywarehouse.presentation.common.composables.SearchInput
import com.example.jaywarehouse.presentation.common.composables.SortBottomSheet
import com.example.jaywarehouse.presentation.common.composables.TopBar
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.SIDE_EFFECT_KEY
import com.example.jaywarehouse.presentation.common.utils.ScreenTransition
import com.example.jaywarehouse.presentation.destinations.ManualPutawayDetailScreenDestination
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayContract
import com.example.jaywarehouse.presentation.manual_putaway.viewmodels.ManualPutawayViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Destination(style = ScreenTransition::class)
@Composable
fun ManualPutawayScreen(
    navigator: DestinationsNavigator,
    putaway: PutawayListGroupedRow,
    viewModel: ManualPutawayViewModel = koinViewModel(
        parameters = {
            parametersOf(putaway)
        }
    )
) {
    val state = viewModel.state
    val onEvent = viewModel::setEvent

    LaunchedEffect(SIDE_EFFECT_KEY) {
        viewModel.effect.collect {
            when(it) {
                ManualPutawayContract.Effect.NavBack -> {
                    navigator.popBackStack()
                }
                is ManualPutawayContract.Effect.NavToPutawayDetail -> {
                    navigator.navigate(ManualPutawayDetailScreenDestination(it.putaway))
                }
            }
        }
    }
    ManualPutawayContent(state,onEvent)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManualPutawayContent(
    state: ManualPutawayContract.State = ManualPutawayContract.State(),
    onEvent: (ManualPutawayContract.Event) -> Unit = {}
) {
    val focusRequester = FocusRequester()

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
        onEvent(ManualPutawayContract.Event.FetchData)
    }
    MyScaffold(
        loadingState = state.loadingState,
        error = state.error,
        onCloseError = {
            onEvent(ManualPutawayContract.Event.OnCloseError)
        },
        onRefresh = {
            onEvent(ManualPutawayContract.Event.OnReloadScreen)
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
                    title = state.putRow?.referenceNumber?:"",
                    subTitle = "Putaway",
                    onBack = {
                        onEvent(ManualPutawayContract.Event.OnNavBack)
                    }
                )
                Spacer(modifier = Modifier.size(20.mdp))
                SearchInput(
                    onSearch = {
                        onEvent(ManualPutawayContract.Event.OnSearch(it.text))
                    },
                    value = state.keyword,
                    isLoading = state.loadingState == Loading.SEARCHING,
                    onSortClick = {
                        onEvent(ManualPutawayContract.Event.OnShowSortList(true))
                    },
                    hideKeyboard = state.lockKeyboard,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.size(20.mdp))
                MyLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    items = state.putaways,
                    itemContent = {_,it->
                        ManualPutawayItem(it){
                            onEvent(ManualPutawayContract.Event.OnPutawayClick(it))
                        }
                    },
                    onReachEnd = {
                        onEvent(ManualPutawayContract.Event.OnReachEnd)
                    },
                    spacerSize = 7.mdp
                )

            }
        }
    }
    if (state.showSortList){
        SortBottomSheet(
            onDismiss = {
                onEvent(ManualPutawayContract.Event.OnShowSortList(false))
            },
            sortOptions = state.sortList,
            selectedSort = state.selectedSort,
            onSelectSort = {
                onEvent(ManualPutawayContract.Event.OnSortChange(it))
            }
        )
    }

}


@Composable
fun ManualPutawayItem(
    model: ManualPutawayRow,
    expandable: Boolean = true,
    onClick: ()->Unit
) {
    var onClickExpand by remember {
        mutableStateOf(expandable)
    }
    BaseListItem(
        onClick = {
            if (expandable) onClick()
            else onClickExpand = !onClickExpand
        },
        item1 = BaseListItemModel("Name",model.productName, R.drawable.vuesax_outline_3d_cube_scan),
        item2 = BaseListItemModel("Product Code",model.productCode, R.drawable.barcode),
        item3 = BaseListItemModel("Barcode",model.productBarcodeNumber?:"", R.drawable.note),
        item4 = BaseListItemModel("Batch No.",model.batchNumber?:"", R.drawable.vuesax_linear_box),
        item5 = BaseListItemModel("Exp Date",model.expireDate?:"", R.drawable.calendar_add),
        item6 = if (onClickExpand)BaseListItemModel("Reference Number",model.referenceNumber?:"",R.drawable.hashtag) else null,
        item7 = if (onClickExpand)BaseListItemModel("Receiving Type",model.receivingTypeTitle?:"",R.drawable.notes) else null,
        item8 = if (onClickExpand)BaseListItemModel("Warehouse Name",model.warehouseName?:"",R.drawable.building) else null,
        quantity = model.total.removeZeroDecimal().toString() + if (model.isWeight) " kg" else "",
        expandable = expandable,
        quantityTitle = "Total",
        scan = model.quantity.removeZeroDecimal().toString() + if (model.isWeight) " kg" else "",
        scanTitle = "scan"
    )
}

@Preview
@Composable
private fun ManualPutawayPreview() {
    ManualPutawayContent()
}