package com.linari.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.receiving.model.ReceivingDetailCountModel
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class CountingInceptionContract {

    data class State(
        val countingDetailRow: ReceivingDetailRow? = null,
        val loadingState: Loading = Loading.NONE,
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue("1"),
        val boxQuantity: TextFieldValue = TextFieldValue(),
        val batchNumber: TextFieldValue = TextFieldValue(),
        val expireDate: TextFieldValue = TextFieldValue(),
        val selectedDate: String = "",
        val showDatePicker: Boolean = false,
        val hideKeyboard: Boolean = false,
        val selectedItem: ReceivingDetailCountModel? = null,
        val count: Int = 0,
        val toast: String = "",
        val error: String = "",
        val details: List<ReceivingDetailCountModel> = emptyList(),
        val page: Int = 1,
        val isAdding: Boolean = false,
        val isDeleting: Boolean = false,
        val isCompleting: Boolean = false,
        val showConfirm: Boolean = false,
        val disableQuantityInPacket: Boolean = false,
        val pcbEnabled: Boolean = true,
        val locationBase: Boolean = true,
        val expEnabled: Boolean = true,
        val batchNumberEnabled: Boolean = true,
        val rowCount: Int = 0,
        val warehouse: WarehouseModel? = null
    ) : UiState

    sealed class Event : UiEvent {
        data object OnSubmit : Event()
        data class OnChangeQuantity(val value: TextFieldValue) : Event()
        data class OnChangeQuantityInPacket(val value: TextFieldValue) : Event()
        data class OnChangeBoxQuantity(val value: TextFieldValue) : Event()
        data class OnChangeBatchNumber(val value: TextFieldValue) : Event()
        data class OnChangeExpireDate(val value: TextFieldValue) : Event()
        data class OnShowDatePicker(val value: Boolean) : Event()
        data object OnAddClick: Event()
        data class OnSelectedDateChange(val date: String) : Event()
        data object OnBack : Event()
        data class OnShowConfirmDialog(val show: Boolean) : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data class OnDeleteCount(val model: ReceivingDetailCountModel) : Event()
        data class OnSelectedItem(val item: ReceivingDetailCountModel?) : Event()
        data object OnReachEnd : Event()
        data object OnAddWeight: Event()
        data object OnRefresh: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}