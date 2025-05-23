package com.example.jaywarehouse.presentation.manual_putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ManualPutawayContract {
    data class State(
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val putaways: List<ManualPutawayRow> = emptyList(),
        val showSortList: Boolean = false,
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName", Order.Asc),
            SortItem("Product Name Z-A", "ProductName", Order.Asc),
            SortItem("Product Code Ascending", "ProductCode", Order.Asc),
            SortItem("Product Code Descending", "ProductCode", Order.Desc),
            SortItem("Barcode Ascending", "Barcode", Order.Desc),
            SortItem("Barcode Descending", "Barcode", Order.Asc)
        ),
        val selectedSort: SortItem = sortList.first(),
        val page: Int = 1,
        val lockKeyboard: Boolean = false
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