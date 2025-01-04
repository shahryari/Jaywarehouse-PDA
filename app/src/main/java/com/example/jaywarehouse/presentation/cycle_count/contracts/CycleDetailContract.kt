package com.example.jaywarehouse.presentation.cycle_count.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailModel
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailRow
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.data.transfer.models.ProductStatusRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CycleDetailContract {
    data class State(
        val cycleRow: CycleRow? = null,
        val detailModel: CycleDetailModel? = null,
        val details: List<CycleDetailRow> = emptyList(),
        val loadingState: Loading = Loading.INIT,
        val selectedCycle: CycleDetailRow? = null,
        val showSortList: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val keyword: TextFieldValue = TextFieldValue(),
        val sortList: List<SortItem> = listOf(
            SortItem("Product Code Ascending", "ProductCode", Order.Asc),
            SortItem("Product Code Descending", "ProductCode",Order.Desc),
            SortItem("Product Name A-Z", "ProductTitle",Order.Asc),
            SortItem("Product Name Z-A", "ProductTitle",Order.Desc),
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
        val showAddDialog: Boolean = false,
        val showSubmit: Boolean = false,
        val showAddButton: Boolean = false,
        //
        val locationCode: TextFieldValue = TextFieldValue(),
        val status: TextFieldValue = TextFieldValue(),
        val statusList: List<ProductStatusRow> = emptyList(),
        val selectedStatus: ProductStatusRow? = null,
        val barcode: TextFieldValue = TextFieldValue(),
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue(),
        val batchNumber: TextFieldValue = TextFieldValue(),
        val expireDate: TextFieldValue = TextFieldValue(),
        val showDatePicker: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnSelectDetail(val detail: CycleDetailRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnSave(val item: CycleDetailRow): Event()
        data object OnAdd: Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnSearch: Event()
        data class OnSortChange(val sortItem: SortItem): Event()
        data class OnShowAddDialog(val show: Boolean) : Event()
        data class OnShowDatePicker(val show: Boolean) : Event()
        data class OnChangeLocationCode(val locationCode: TextFieldValue) : Event()
        data class OnChangeStatus(val status: TextFieldValue) : Event()
        data class OnChangeExpireDate(val expireDate: TextFieldValue) : Event()
        data class OnChangeQuantity(val quantity: TextFieldValue) : Event()
        data class OnChangeQuantityInPacket(val quantityInPacket: TextFieldValue) : Event()
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnSelectStatus(val status: ProductStatusRow) : Event()
        data object OnEndTaskClick : Event()
        data class OnShowSubmit(val show: Boolean) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
    }
}