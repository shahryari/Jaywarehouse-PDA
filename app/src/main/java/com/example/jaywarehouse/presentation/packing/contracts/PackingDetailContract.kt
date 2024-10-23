package com.example.jaywarehouse.presentation.packing.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.packing.model.PackingDetailModel
import com.example.jaywarehouse.data.packing.model.PackingDetailRow
import com.example.jaywarehouse.data.packing.model.PackingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PackingDetailContract {
    data class State(
        val packingRow: PackingRow? = null,
        val packedScanItems: PackingDetailModel? = null,
        val packingDetails: List<PackingDetailRow> = emptyList(),
        val barcode: TextFieldValue = TextFieldValue(),
        val selectedItem: Int? = null,
        val toast: String = "",
        val loadingState: Loading = Loading.NONE,
        val isScanning: Boolean = false,
        val showSubmitDialog: Boolean = false,
        val showSubmitAndNewDialog: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnBarcodeChange(val barcode: TextFieldValue) : Event()
        data object HideToast : Event()
        data class SelectedItem(val id: Int?) : Event()
        data object CloseError : Event()
        data object ScanBarcode: Event()
        data class OnRemove(val id: Int) : Event()
        data class OnShowSubmit(val show: Boolean) : Event()
        data class OnShowSubmitAndNew(val show: Boolean) : Event()
        data object OnSubmit : Event()
        data object OnSubmitAndNew: Event()
        data object OnNavBack : Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack : Effect()
    }
}