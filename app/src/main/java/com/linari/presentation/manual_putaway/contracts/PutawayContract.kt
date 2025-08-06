package com.linari.presentation.manual_putaway.contracts

import com.linari.data.putaway.model.PutawayListGroupedModel
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class PutawayContract {
    data class State(
        val readToPut: PutawayListGroupedModel? = null,
        val puts: List<PutawayListGroupedRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Supplier A-Z", "SupplierName",Order.Asc),
            SortItem("Supplier Z-A", "SupplierName",Order.Desc),
            SortItem("Reference Number Ascending", "ReferenceNumber",Order.Asc),
            SortItem("Reference Number Descending", "ReferenceNumber",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        val rowCount: Int = 0
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavToPutawayDetail(val readyToPutRow: PutawayListGroupedRow) : Event()
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
        data class NavToPutawayDetail(val readyToPutRow: PutawayListGroupedRow, val fillLocation: Boolean) : Effect()
        data object NavBack: Effect()
    }
}