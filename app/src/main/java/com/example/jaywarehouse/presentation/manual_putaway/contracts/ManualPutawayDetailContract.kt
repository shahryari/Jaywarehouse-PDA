package com.example.jaywarehouse.presentation.manual_putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayDetailRow
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState


class ManualPutawayDetailContract {

    data class State(
        val putaway: ManualPutawayRow? = null,
        val loadingState: Loading = Loading.NONE,
        val lockKeyboard: Boolean = false,
        val keyword: TextFieldValue = TextFieldValue(),
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue(),
        val locationCode: TextFieldValue = TextFieldValue(),
        val count: Int = 0,
        val error: String = "",
        val toast: String = "",
        val details: List<ManualPutawayDetailRow> = emptyList(),
        val page: Int = 1,
        val sort: String = "",
        val showSortList: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnQuantityChange(val quantity: TextFieldValue) : Event()
        data class OnQuantityInPacketChange(val quantity: TextFieldValue) : Event()
        data class OnLocationCodeChange(val locationCode: TextFieldValue) : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnCloseError: Event()
        data object HideToast: Event()
        data object OnSubmit: Event()
        data object OnRefresh: Event()
        data object OnReachEnd: Event()
        data object OnNavBack: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}