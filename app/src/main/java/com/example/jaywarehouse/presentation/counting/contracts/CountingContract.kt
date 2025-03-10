package com.example.jaywarehouse.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.receiving.model.ReceivingModel
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CountingContract {
    data class State(
        val receivingModel: ReceivingModel? = null,
        val countingList: List<ReceivingRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val showSortList: Boolean = false,
        val sortList: List<SortItem> = listOf(
            SortItem("Receiving Date closed to now", "ReceivingDate",Order.Desc),
            SortItem("Receiving Date farthest from now", "ReceivingDate",Order.Asc),
            SortItem("Reference Number Descending", "ReferenceNumber",Order.Desc),
            SortItem("Reference Number Ascending", "ReferenceNumber",Order.Asc),
            SortItem("Supplier Name Descending", "Supplier",Order.Desc),
            SortItem("Supplier Name Ascending", "Supplier",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val order: String = "desc",
        val page: Int = 1,
        val error: String = "",
        val lockKeyboard: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavToReceivingDetail(val receivingRow: ReceivingRow) : Event()
        data object ClearError : Event()
        data class OnSelectSort(val sort: SortItem) : Event()
        data class OnSelectOrder(val order: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnListEndReached : Event()
        data object OnRefresh: Event()
        data class OnSearch(val keyword: String): Event()
        data object FetchData: Event()
        data object OnBackPressed: Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavToReceivingDetail(val receivingRow: ReceivingRow) : Effect()
        data object NavBack : Effect()
    }
}