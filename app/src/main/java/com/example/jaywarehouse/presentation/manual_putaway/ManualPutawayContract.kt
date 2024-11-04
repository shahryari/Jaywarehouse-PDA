package com.example.jaywarehouse.presentation.manual_putaway

import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.preferences.protobuf.Internal.BooleanList
import com.example.jaywarehouse.data.putaway.model.PutawaysRow
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ManualPutawayContract {
    data class State(
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val putaways: List<ReadyToPutRow> = emptyList(),
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
        data class OnPutawayClick(val putaway: ReadyToPutRow) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavToPutawayDetail(val putaway: ReadyToPutRow) : Effect()
        data object NavBack : Effect()
    }
}