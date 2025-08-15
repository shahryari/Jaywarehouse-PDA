package com.linari.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.picking.models.PickingListGroupedModel
import com.linari.data.picking.models.PickingListGroupedRow
import com.linari.data.putaway.model.PutawayListGroupedModel
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class PickingContract {
    data class State(
        val pickingModel: PickingListGroupedModel? = null,
        val pickings: List<PickingListGroupedRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Customer Name A-Z","CustomerName", Order.Asc),
            SortItem("Customer Name Z-A","CustomerName", Order.Desc),
            SortItem("Customer Code A-Z","CustomerCode", Order.Asc),
            SortItem("Customer Code Z-A","CustomerCode", Order.Desc),
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        val rowCount: Int = 0,
        val warehouse: WarehouseModel? = null,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavToPickingDetail(val pick: PickingListGroupedRow) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()

    }

    sealed class Effect: UiSideEffect {
        data class NavToPickingDetail(val pick: PickingListGroupedRow) : Effect()
        data object NavBack: Effect()
    }
}