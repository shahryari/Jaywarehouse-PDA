package com.linari.presentation.pallet.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class PalletProductContract {
    data class State(
        val keyword: String = "",
        val page : Int = 1,
        val productList: List<PalletManifestProductRow> = emptyList(),
        val loadingState: Loading = Loading.NONE,
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z","ProductName", Order.Asc),
            SortItem("Product Name Z-A","ProductName", Order.Desc),
            SortItem("Product Code Ascending","ProductCode",Order.Asc),
            SortItem("Product Code Descending","ProductCode",Order.Desc),
            SortItem("Barcode Ascending","ProductBarcodeNumber",Order.Asc),
            SortItem("Barcode Descending","ProductBarcodeNumber",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val pallet: PalletConfirmRow? = null,
        val showConfirm: Boolean = false,
        val showSortList: Boolean = false,
        val isConfirming: Boolean = false,
        val error: String = "",
        val toast: String = "",
        val rowCount: Int = 0,
        val lockKeyboard: Boolean = false,
        val hasBoxOnShipping: Boolean = false,
        val bigQuantity: TextFieldValue = TextFieldValue(),
        val smallQuantity: TextFieldValue = TextFieldValue(),
        val warehouse: WarehouseModel? = null,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSearch(val keyword: String) : Event()
        data object OnRefresh: Event()
        data object OnReachEnd: Event()
        data object FetchData: Event()
        data class OnSelectSort(val sort: SortItem) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnShowConfirm(val show: Boolean) : Event()
        data object CloseError: Event()
        data object CloseToast: Event()
        data object OnNavBack: Event()
        data object OnConfirm: Event()
        
        data class ChangeBigQuantity(val quantity: TextFieldValue) : Event()
        data class ChangeSmallQuantity(val quantity: TextFieldValue) : Event()
        data object OnConfirmBox : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}