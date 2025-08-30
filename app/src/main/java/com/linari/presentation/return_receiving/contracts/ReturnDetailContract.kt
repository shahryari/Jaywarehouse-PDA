package com.linari.presentation.return_receiving.contracts

import androidx.compose.material.TextField
import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.return_receiving.models.ReturnDetailRow
import com.linari.data.return_receiving.models.ReturnRow
import com.linari.data.transfer.models.ProductStatusRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class ReturnDetailContract {
    data class State(
        val list: List<ReturnDetailRow> = emptyList(),
        val rowCount: Int = 0,
        val page: Int = 1,
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName",Order.Asc),
            SortItem("Product Name Z-A", "ProductName",Order.Desc),
            SortItem("Product Code Ascending", "ProductCode",Order.Asc),
            SortItem("Product Code Descending", "ProductCode",Order.Desc),
            SortItem("Barcode Ascending", "Barcode",Order.Asc),
            SortItem("Barcode Descending", "Barcode",Order.Desc)
        ),
        val sortItem: SortItem = sortList.first(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val showSortList: Boolean = false,
        val showAdd: Boolean = false,
        val selectedForDelete: ReturnDetailRow? = null,
        val error: String = "",
        val toast: String = "",
        val master: ReturnRow? = null,
        val warehouse: WarehouseModel? = null,
        val lockKeyboard: Boolean = false,
        val quantity: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
        val productStatus: ProductStatusRow? = null,
        val productStatusList: List<ProductStatusRow> = emptyList(),
        val showProductStatusList: Boolean = false,
    ) : UiState
    sealed class Event : UiEvent{
        data object FetchData: Event()
        data object OnRefresh: Event()
        data object OnReachEnd: Event()
        data class OnSearch(val keyword: String) : Event()
        data object CloseError: Event()
        data object CloseToast: Event()
        data class ShowSortList(val show: Boolean) : Event()
        data class ShowAdd(val show: Boolean) : Event()
        data class OnSelectForDelete(val returnDetailRow: ReturnDetailRow?) : Event()
        data object OnAdd: Event()
        data object OnConfirmDelete: Event()
        data object OnNavBack: Event()
        data class OnSortChange(val sortItem: SortItem) : Event()
        data class ChangeBarcode(val barcode: TextFieldValue) : Event()
        data class ChangeQuantity(val quantity: TextFieldValue) : Event()
        data class OnSelectStatus(val status: ProductStatusRow?) : Event()
        data class OnShowStatusList(val show: Boolean) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}