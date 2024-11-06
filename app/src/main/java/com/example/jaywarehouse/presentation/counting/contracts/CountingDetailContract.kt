package com.example.jaywarehouse.presentation.counting.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CountingDetailContract {
    data class State(
        val countingRow: ReceivingRow? = null,
        val barcode: TextFieldValue = TextFieldValue(),
        val countingDetailModel: ReceivingDetailModel? = null,
        val countingDetailRow: List<ReceivingDetailRow> = emptyList(),
        val loadingState: Loading = Loading.NONE,
        val isScanLoading: Boolean = false,
        val selectedDetail: String? = null,
        val showClearIcon: Boolean = false,
        val keyword: TextFieldValue = TextFieldValue(),
        val toast: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn",Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Latest Product A-Z", "ProductCode",Order.Asc),
            SortItem("Latest Product Z-A", "ProductCode",Order.Desc),
            SortItem("Barcode Closed to now", "Barcode",Order.Desc),
            SortItem("Barcode farthest from now", "Barcode",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val order: String = Order.Asc.value,
        val page: Int = 1,
        val showSortList: Boolean = false,
        val showConfirm: Boolean = false,
        val error: String = "",
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data object CloseError: Event()
        data object OnNavBack : Event()
        data object OnClearBarcode: Event()
        data object HideToast: Event()
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnSelectDetail(val barcode: String?) : Event()
        data class OnSelectSort(val sort: SortItem) : Event()
        data class OnSelectOrder(val order: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnReachedEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data class OnDetailClick(val detail: ReceivingDetailRow) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
        data class OnNavToInception(val detail: ReceivingDetailRow): Effect()
    }
}