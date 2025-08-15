package com.linari.presentation.loading.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.checking.models.CheckingListModel
import com.linari.data.loading.models.LoadingListGroupedRow
import com.linari.data.pallet.model.PalletConfirmRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class LoadingDetailContract {
    data class State(
        val loadingRow: LoadingListGroupedRow? = null,
        val loadingDetailModel: CheckingListModel? = null,
        val details: List<PalletConfirmRow> = emptyList(),
        val loadingState: Loading = Loading.NONE,
        val selectedLoading: PalletConfirmRow? = null,
        val showSortList: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val keyword: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Pallet Barcode A-Z","PalletBarcode", Order.Asc),
            SortItem("Pallet Barcode Z-A","PalletBarcode", Order.Desc),
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
        val rowCount: Int = 0,
        val warehouse: WarehouseModel? = null
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSelectDetail(val detail: PalletConfirmRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnConfirmLoading(val item: PalletConfirmRow): Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnSearch(val keyword: String): Event()
        data class OnSortChange(val sortItem: SortItem): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
    }
}