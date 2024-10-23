package com.example.jaywarehouse.presentation.shipping.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.DriverModelItem
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ShippingContract {
    data class State(
        val keyword: TextFieldValue = TextFieldValue(),
        val page: Int = 1,
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val shippingModel: ShippingModel? = null,
        val shippingList: List<ShippingRow> = emptyList(),
        val showAddDialog: Boolean = false,
        val showFilterList: Boolean = false,
        val shippingNumber: TextFieldValue = TextFieldValue(),
        val selectedDriver: DriverModelItem? = null,
        val driverList: DriverModel? = null,
        val selectedShip: Int? = null,
        val showPopup: Boolean = false,
        val isShipping: Boolean = false,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val driverName: TextFieldValue = TextFieldValue()
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnOrderChange(val order: String) : Event()
        data class OnShippingClick(val shippingRow: ShippingRow) : Event()
        data class OnShowFilterList(val showFilterList: Boolean) : Event()
        data object OnClearError : Event()
        data object OnAddClick : Event()
        data class OnRemoveClick(val packingId: Int) : Event()
        data class OnShowAddDialog(val showAddDialog: Boolean) : Event()
        data class OnShippingNumberChange(val shippingNumber: TextFieldValue) : Event()
        data class OnDriverChange(val driver: DriverModelItem) : Event()
        data object HideToast : Event()
        data class OnSelectShip(val packingId: Int?) : Event()
        data class OnShowPopup(val show: Boolean) : Event()
        data object OnReachEnd : Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object FetchData: Event()
        data class OnDriverNameChange(val name: TextFieldValue) : Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavigateToShippingDetail(val shippingRow: ShippingRow) : Effect()
    }
}