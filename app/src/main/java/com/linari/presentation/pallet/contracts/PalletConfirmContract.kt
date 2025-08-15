package com.linari.presentation.pallet.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.pallet.model.PalletConfirmModel
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.data.pallet.model.PalletManifestProductRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState
import com.linari.presentation.pallet.contracts.PalletProductContract.Event

class PalletConfirmContract {
    data class State(
        val palletModel: PalletConfirmModel? = null,
        val palletList: List<PalletConfirmRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Pallet Barcode A-Z", "PalletBarcode", Order.Asc),
            SortItem("Pallet Barcode Z-A", "PalletBarcode", Order.Desc),
            SortItem("Customer Name A-Z", "CustomerName", Order.Asc),
            SortItem("Customer Name Z-A", "CustomerName", Order.Desc),
//            SortItem("Customer Code A-Z", "CustomerCode", Order.Asc),
//            SortItem("Customer Code Z-A", "CustomerCode", Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        val isConfirming: Boolean = false,
        val selectedPallet: PalletConfirmRow? = null,
        val toast: String = "",
        val rowCount: Int = 0,
        val hasBoxOnShipping: Boolean = false,

        val bigQuantity: TextFieldValue = TextFieldValue(),
        val smallQuantity: TextFieldValue = TextFieldValue(),
        val warehouse: WarehouseModel? = null,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSelectPallet(val pallet: PalletConfirmRow?) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()
        data class OnNavToDetail(val pallet: PalletConfirmRow) : Event()
        data class ConfirmPallet(val pallet: PalletConfirmRow) : Event()
        data object HideToast: Event()


        data class ChangeBigQuantity(val quantity: TextFieldValue) : Event()
        data class ChangeSmallQuantity(val quantity: TextFieldValue) : Event()
        data class OnConfirmBox(val pallet: PalletConfirmRow) : Event()

    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
        data class NavToDetail(val pallet: PalletConfirmRow) : Effect()
    }
}