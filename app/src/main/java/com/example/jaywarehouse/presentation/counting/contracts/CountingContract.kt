package com.example.jaywarehouse.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.receiving.model.ReceivingModel
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CountingContract {
    data class State(
        val receivingModel: ReceivingModel? = null,
        val countingList: List<ReceivingRow> = emptyList(),
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val showSortList: Boolean = false,
        val sort: String = "CreatedOn",
        val order: String = "Desc",
        val page: Int = 1,
        val error: String = "",
        val lockKeyboard: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnNavToReceivingDetail(val receivingRow: ReceivingRow) : Event()
        data object ClearError : Event()
        data class OnSelectSort(val sort: String) : Event()
        data class OnSelectOrder(val order: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnListEndReached : Event()
        data object OnRefresh: Event()
        data object OnSearch: Event()
        data object FetchData: Event()
        data object OnBackPressed: Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavToReceivingDetail(val receivingRow: ReceivingRow) : Effect()
        data object NavBack : Effect()
    }
}