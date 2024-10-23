package com.example.jaywarehouse.presentation.putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.putaway.model.ReadyToPutModel
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract

class PutawayContract {
    data class State(
        val readToPut: ReadyToPutModel? = null,
        val puts: List<ReadyToPutRow> = emptyList(),
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnNavToPutawayDetail(val readyToPutRow: ReadyToPutRow) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: String) : Event()
        data class OnChangeOrder(val order: String) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()

    }

    sealed class Effect: UiSideEffect {
        data class NavToPutawayDetail(val readyToPutRow: ReadyToPutRow,val fillLocation: Boolean) : Effect()
    }
}