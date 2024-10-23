package com.example.jaywarehouse.presentation.shipping.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.shipping.models.ShippingDetailModel
import com.example.jaywarehouse.data.shipping.models.ShippingDetailRow
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ShippingDetailContract {
    data class State(
        val keyword: TextFieldValue = TextFieldValue(),
        val page: Int = 1,
        val showFilterList: Boolean = false,
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val isScanning: Boolean = false,
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val shippingDetailModel: ShippingDetailModel? = null,
        val shippingDetailList: List<ShippingDetailRow> = emptyList(),
        val shippingRow: ShippingRow? = null,
        val selectedShippingDetail: Int? = null,
        val toast: String = "",
        val showInvoiceConfirm: Boolean = false,
        val barcode: TextFieldValue = TextFieldValue(),
        val lockKeyboard: Boolean = false,
        val showAll: Boolean = false,
    ) : UiState

    sealed class Event: UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnOrderChange(val order: String) : Event()
        data class OnShowFilterList(val showFilterList: Boolean) : Event()
        data class OnShowInvoiceConfirm(val show:Boolean) : Event()
        data object OnClearError : Event()
        data class OnBarcodeChange(val barcode: TextFieldValue) : Event()
        data object ScanBarcode: Event()
        data object HideToast: Event()
        data object OnNavBack: Event()
        data object OnInvoice: Event()
        data class OnRemoveShippingDetail(val packingId: Int) : Event()
        data class OnSelectShippingDetail(val packingId: Int?) : Event()
        data object OnReachEnd : Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object OnShowAll: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack : Effect()
    }
}