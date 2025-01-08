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
            SortItem("Created On closed to now", "CreatedOn",Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Receiving Number Descending", "Receiving",Order.Desc),
            SortItem("Receiving Number Ascending", "Receiving",Order.Asc),
            SortItem("Most Progress", "Progress",Order.Desc),
            SortItem("Least Progress", "Progress",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val order: String = "Desc",
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