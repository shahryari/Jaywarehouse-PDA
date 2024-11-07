package com.example.jaywarehouse.presentation.manual_putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ManualPutawayContract {
    data class State(
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val putaways: List<ManualPutawayRow> = emptyList(),
        val showSortList: Boolean = false,
        val sortList: Map<String,String> = emptyMap(),
        val selectedSort: String = "CreatedOn",
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent{
        data object OnNavBack: Event()
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data object OnSearch : Event()
        data object OnReloadScreen : Event()
        data object OnReachEnd : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnCloseError: Event()
        data class OnPutawayClick(val putaway: ManualPutawayRow) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavToPutawayDetail(val putaway: ManualPutawayRow) : Effect()
        data object NavBack : Effect()
    }
}