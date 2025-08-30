package com.linari.presentation.return_receiving.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.return_receiving.models.CustomerRow
import com.linari.data.return_receiving.models.OwnerInfoRow
import com.linari.data.return_receiving.models.ReturnRow
import com.linari.data.shipping.models.ShippingCustomerRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class ReturnReceivingContract {
    data class State(
        val list: List<ReturnRow> = emptyList(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val page: Int = 1,
        val sortList: List<SortItem> = listOf(
            SortItem("Customer Name A-Z", "PartnerName",Order.Asc),
            SortItem("Customer Name Z-A", "PartnerName",Order.Desc),
            SortItem("Reference Number Ascending", "ReferenceNumber",Order.Asc),
            SortItem("Reference Number Descending", "ReferenceNumber",Order.Desc),
            SortItem("Receiving Number Ascending","ReceivingNumber",Order.Asc),
            SortItem("Receiving Number Descending","ReceivingNumber", order = Order.Desc)
        ),
        val sortItem: SortItem = sortList.first(),
        val showSortList: Boolean = false,
        val keyword: String = "",
        val selectedForDelete: ReturnRow? = null,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val showAdd: Boolean = false,
        val rowCount: Int = 0,
        val toast: String = "",
        val warehouse: WarehouseModel? = null,
        val lockKeyboard: Boolean = false,
        val customer: CustomerRow? = null,
        val receivingDate: String = "",
        val receivingShowDate: TextFieldValue = TextFieldValue(),
        val referenceNumber: TextFieldValue = TextFieldValue(),
        val ownerInfo: OwnerInfoRow? = null,
        val ownerInfoList: List<OwnerInfoRow> = emptyList(),
        val showOwnerList: Boolean = false,
        val customerList: List<CustomerRow> = emptyList(),
        val showCustomerList: Boolean = false,
        val showDatePicker: Boolean = false,
    ) : UiState

    sealed class Event: UiEvent {
        data class OnSearch(val keyword: String) : Event()
        data object OnRefresh: Event()
        data object FetchData: Event()
        data object ReachEnd: Event()
        data object CloseError: Event()
        data object CloseToast: Event()
        data class ShowSortList(val show: Boolean) : Event()
        data object OnNavBack: Event()
        data class SelectForDelete(val returnRow: ReturnRow?) : Event()
        data object ConfirmDelete: Event()
        data class ShowAdd(val show: Boolean) : Event()
        data object OnAdd: Event()
        data class OnNavToDetail(val model: ReturnRow) : Event()
        data class OnSortChange(val sortItem: SortItem) : Event()
        data class OnShowCustomerList(val show: Boolean) : Event()
        data class ChangeReferenceNumber(val referenceNumber: TextFieldValue) : Event()
        data class OnShowOwnerList(val show: Boolean) : Event()
        data class OnSelectCustomer(val customer: CustomerRow?) : Event()
        data class OnSelectOwner(val owner: OwnerInfoRow) : Event()
        data class ChangeReceivingDate(val receivingDate: String,val showDate: String) : Event()
        data class ShowDatePicker(val show: Boolean) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
        data class NavToDetail(val model: ReturnRow) : Effect()
    }
}