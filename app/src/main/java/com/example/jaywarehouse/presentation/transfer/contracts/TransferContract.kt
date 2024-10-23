package com.example.jaywarehouse.presentation.transfer.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.transfer.models.LocationTransferModel
import com.example.jaywarehouse.data.transfer.models.LocationTransferRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class TransferContract {
    data class State(
        val isPick: Boolean = true,
        val showTransferBox: Boolean = false,
        val showBoxButton: Boolean = false,
        val showTransferItem: Boolean = false,
        val transferModel: LocationTransferModel? = null,
        val transferList: List<LocationTransferRow> = emptyList(),
        val keyword: TextFieldValue = TextFieldValue(),
        val boxNumber: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
        val locationCode: TextFieldValue = TextFieldValue(),
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val page: Int = 1,
        val isTransferring: Boolean =false,
        val loadingState: Loading = Loading.NONE,
        val showSortList: Boolean = false,
        val error: String = "",
        val toast: String = "",
        val lockKeyboard: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnBoxNumberChange(val boxNumber: TextFieldValue) : Event()
        data class OnBarcodeChange(val barcode: TextFieldValue) : Event()
        data class OnLocationCodeChange(val locationCode: TextFieldValue) : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnOrderChange(val order: String) : Event()
        data class ShowSortList(val show: Boolean) : Event()
        data object OnSearch : Event()
        data object CloseError : Event()
        data object HideToast: Event()
        data class OnShowTransferBox(val show: Boolean) : Event()
        data class OnShowTransferItem(val show: Boolean) : Event()
        data object OnTransferBox : Event()
        data object OnTransferItem : Event()
        data class ShowTransferButton(val show: Boolean) : Event()
        data object OnReachToEnd: Event()
        data object OnRefresh: Event()
        data object LoudSettings: Event()
    }

    sealed class Effect : UiSideEffect
}