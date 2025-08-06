package com.linari.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.receiving.model.ReceivingModel
import com.linari.data.receiving.model.ReceivingRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

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
            SortItem("Reference Number Ascending", "ReferenceNumber",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val order: String = "desc",
        val page: Int = 1,
        val error: String = "",
        val lockKeyboard: Boolean = false,
        val rowCount: Int = 0
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