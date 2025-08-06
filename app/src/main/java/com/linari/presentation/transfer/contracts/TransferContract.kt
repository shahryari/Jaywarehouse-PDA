package com.linari.presentation.transfer.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.data.transfer.models.ProductStatusRow
import com.linari.data.transfer.models.TransferModel
import com.linari.data.transfer.models.TransferRow
import com.linari.data.transfer.models.WarehouseLocationRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class TransferContract {
    data class State(
        val transferModel: TransferModel? = null,
        val transferList: List<TransferRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val locationKeyword: TextFieldValue = TextFieldValue(),
        val productCodeKeyword: TextFieldValue = TextFieldValue(),
        val barcodeKeyword: TextFieldValue = TextFieldValue(),
        val error: String = "",
        val toast: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Barcode Ascending","ProductBarcodeNumber",Order.Asc),
            SortItem("Barcode Descending","ProductBarcodeNumber",Order.Desc),
            SortItem("Product Code Ascending","ProductCode",Order.Asc),
            SortItem("Product Code Descending","ProductCode",Order.Desc),
            SortItem("Location A-Z","LocationCode",Order.Asc),
            SortItem("Location Z-A","LocationCode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val selectedTransfer: TransferRow? = null,
        val showSortList: Boolean = false,
        val hasTransfer: Boolean = false,
        val lockKeyboard: Boolean = false,
        //transfer
        val destination: TextFieldValue = TextFieldValue(),
        val productStatus: TextFieldValue = TextFieldValue(),
        val quantity: TextFieldValue = TextFieldValue(),
        val expirationDate: TextFieldValue = TextFieldValue(),
        val showDatePicker: Boolean = false,
        val selectedProductStatus: ProductStatusRow? = null,
        val productStatusList: List<ProductStatusRow> = emptyList(),
        val selectedLocation: WarehouseLocationRow? = null,
        val locationList: List<WarehouseLocationRow> = emptyList(),
        val isSaving: Boolean = false,
        val rowCount: Int = 0,
        val showStatusList: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data object ClearError: Event()
        data object HideToast: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()
        data class ChangeLocationKeyword(val keyword: TextFieldValue) : Event()
        data class ChangeProductCodeKeyword(val keyword: TextFieldValue) : Event()
        data class ChangeBarcodeKeyword(val keyword: TextFieldValue) : Event()
        data class OnSelectTransfer(val transferRow: TransferRow?) : Event()
        //transfer
        data class OnChangeDestination(val destination: TextFieldValue) : Event()
        data class OnChangeProductStatus(val productStatus: TextFieldValue) : Event()
        data class OnChangeQuantity(val quantity: TextFieldValue) : Event()
        data class OnChangeExpirationDate(val expirationDate: TextFieldValue) : Event()
        data class OnTransfer(val row: TransferRow) : Event()
        data class OnShowDatePicker(val show: Boolean) : Event()
        data class OnSelectProductStatus(val productStatus: ProductStatusRow?) : Event()
        data class OnSelectWarehouseLocation(val location: WarehouseLocationRow?) : Event()
        data class OnShowStatusList(val show: Boolean) : Event()

    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
    }
}