package com.linari.presentation.manual_putaway.contracts

import com.linari.data.auth.models.WarehouseModel
import com.linari.data.manual_putaway.models.ManualPutawayRow
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class ManualPutawayContract {
    data class State(
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val putaways: List<ManualPutawayRow> = emptyList(),
        val putRow: PutawayListGroupedRow? = null,
        val showSortList: Boolean = false,
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName", Order.Asc),
            SortItem("Product Name Z-A", "ProductName", Order.Desc),
            SortItem("Product Code Ascending", "ProductCode", Order.Asc),
            SortItem("Product Code Descending", "ProductCode", Order.Desc),
            SortItem("Barcode Ascending", "Barcode", Order.Asc),
            SortItem("Barcode Descending", "Barcode", Order.Desc)
        ),
        val selectedSort: SortItem = sortList.first(),
        val page: Int = 1,
        val lockKeyboard: Boolean = false,
        val rowCount: Int = 0,
        val warehouse: WarehouseModel? = null
    ) : UiState

    sealed class Event : UiEvent{
        data object OnNavBack: Event()
        data class OnSearch(val keyword: String) : Event()
        data object OnReloadScreen : Event()
        data object FetchData: Event()
        data object OnReachEnd : Event()
        data class OnSortChange(val sort: SortItem) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnCloseError: Event()
        data class OnPutawayClick(val putaway: ManualPutawayRow) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavToPutawayDetail(val putaway: ManualPutawayRow) : Effect()
        data object NavBack : Effect()
    }
}