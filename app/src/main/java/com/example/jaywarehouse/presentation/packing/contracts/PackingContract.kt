package com.example.jaywarehouse.presentation.packing.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.packing.model.PackingCustomerModel
import com.example.jaywarehouse.data.packing.model.PackingCustomerModelItem
import com.example.jaywarehouse.data.packing.model.PackingModel
import com.example.jaywarehouse.data.packing.model.PackingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PackingContract {
    data class State(
        val keyword: TextFieldValue = TextFieldValue(),
        val page: Int = 1,
        val sort: String = "CreatedOn",
        val order: String = Order.Asc.value,
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val packingModel: PackingModel? = null,
        val packings: List<PackingRow> = emptyList(),
        val showAddDialog: Boolean = false,
        val showFilterList: Boolean = false,
        val packingNumber: TextFieldValue = TextFieldValue(),
        val customerName: TextFieldValue = TextFieldValue(),
        val selectedCustomer: PackingCustomerModelItem? = null,
        val customerList: PackingCustomerModel? = null,
        val selectedPack: Int? = null,
        val showPopup: Boolean = false,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val isPacking: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnKeywordChange(val keyword: TextFieldValue) : Event()
        data class OnSortChange(val sort: String) : Event()
        data class OnOrderChange(val order: String) : Event()
        data class OnPackingClick(val packingRow: PackingRow) : Event()
        data class OnShowFilterList(val showFilterList: Boolean) : Event()
        data object OnClearError : Event()
        data object OnAddClick : Event()
        data class OnRemoveClick(val packingId: Int) : Event()
        data class OnShowAddDialog(val showAddDialog: Boolean) : Event()
        data class OnPackingNumberChange(val packingNumber: TextFieldValue) : Event()
        data class OnCustomerChange(val customer: PackingCustomerModelItem) : Event()
        data object HideToast : Event()
        data class OnCustomerNameChange(val customerName: TextFieldValue) : Event()
        data class OnSelectPack(val packingId: Int?) : Event()
        data class OnShowPopup(val show: Boolean) : Event()
        data object OnReachedEnd : Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object FetchData: Event()
    }

    sealed class Effect : UiSideEffect {
        data class NavigateToPackingDetail(val packingRow: PackingRow) : Effect()
    }
}