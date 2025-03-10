package com.example.jaywarehouse.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CountingInceptionContract {

    data class State(
        val countingDetailRow: ReceivingDetailRow? = null,
        val loadingState: Loading = Loading.NONE,
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue("1"),
        val batchNumber: TextFieldValue = TextFieldValue(),
        val expireDate: TextFieldValue = TextFieldValue(),
        val showDatePicker: Boolean = false,
        val hideKeyboard: Boolean = false,
        val selectedItem: ReceivingDetailCountModel? = null,
        val selectedIndex: Int? = null,
        val count: Int = 0,
        val toast: String = "",
        val error: String = "",
        val details: List<ReceivingDetailCountModel> = emptyList(),
        val showConfirm: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data object OnSubmit : Event()
        data class OnChangeQuantity(val value: TextFieldValue) : Event()
        data class OnChangeQuantityInPacket(val value: TextFieldValue) : Event()
        data class OnChangeBatchNumber(val value: TextFieldValue) : Event()
        data class OnChangeExpireDate(val value: TextFieldValue) : Event()
        data class OnShowDatePicker(val value: Boolean) : Event()
        data object OnAddClick: Event()
        data object OnBack : Event()
        data class OnShowConfirmDialog(val show: Boolean) : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data class OnDeleteCount(val model: ReceivingDetailCountModel) : Event()
        data class OnSelectedItem(val item: ReceivingDetailCountModel?, val index: Int? = null) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}