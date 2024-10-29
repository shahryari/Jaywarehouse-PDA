package com.example.jaywarehouse.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.model.ReceivingInceptionDetailRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CountingInceptionContract {

    data class State(
        val countingDetailRow: ReceivingDetailRow? = null,
        val loadingState: Loading = Loading.NONE,
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue(),
        val batchNumber: TextFieldValue = TextFieldValue(),
        val expireDate: TextFieldValue = TextFieldValue(),
        val count: Int = 0,
        val toast: String = "",
        val error: String = "",
        val details: List<ReceivingInceptionDetailRow> = emptyList()
    ) : UiState

    sealed class Event : UiEvent {
        data object OnSubmit : Event()
        data object OnBack : Event()
    }

    sealed class Effect : UiSideEffect {

    }
}