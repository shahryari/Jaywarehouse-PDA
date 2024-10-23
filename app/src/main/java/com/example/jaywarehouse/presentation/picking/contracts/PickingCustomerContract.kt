package com.example.jaywarehouse.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.picking.models.CustomerToPickModel
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PickingCustomerContract {
    data class State(
        val customerToPick: CustomerToPickModel? = null,
        val customerToPicks: List<CustomerToPickRow> = emptyList(),
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
        data class OnKeyWordChange(val keyword: TextFieldValue) : Event()
        data class OnPickClick(val customerToPickRow: CustomerToPickRow) : Event()
        data object OnNavBAck : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data class OnSortChanged(val sort: String) : Event()
        data class OnOrderChanged(val order: String) : Event()
        data object ClearError : Event()
        data object OnReachEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object FetchData: Event()
    }

    sealed class Effect : UiSideEffect{
        data class NavigateToPicking(val customerToPickRow: CustomerToPickRow) : Effect()
        data object NavigateBack : Effect()
    }
}