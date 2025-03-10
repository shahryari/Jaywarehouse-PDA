package com.example.jaywarehouse.presentation.loading.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

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