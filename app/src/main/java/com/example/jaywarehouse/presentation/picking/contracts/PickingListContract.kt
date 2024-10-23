package com.example.jaywarehouse.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.data.picking.models.ReadyToPickModel
import com.example.jaywarehouse.data.picking.models.ReadyToPickRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PickingListContract {
    data class State(
        val pickModel: ReadyToPickModel? = null,
        val pickingList: List<ReadyToPickRow> = emptyList(),
        val customer: CustomerToPickRow? = null,
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val page: Int = 1,
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnPickClick(val readyToPickRow: ReadyToPickRow) : Event()
        data object OnNavBack : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data class OnSortChanged(val sort: String) : Event()
        data class OnOrderChanged(val order: String) : Event()
        data object ClearError : Event()
        data object OnReachToEnd: Event()
        data object OnSearch: Event()
        data object FetchData: Event()
        data object OnRefresh: Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavigateToPickingDetail(val readyToPickRow: ReadyToPickRow,val fillLocation: Boolean) : Effect()
        data object NavigateBack : Effect()
    }
}