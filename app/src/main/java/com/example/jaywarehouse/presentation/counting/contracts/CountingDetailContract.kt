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
        val keyword: String = "",
        val toast: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName",Order.Asc),
            SortItem("Product Name Z-A", "ProductName",Order.Desc),
            SortItem("Product Code Ascending", "ProductCode",Order.Asc),
            SortItem("Product Code Descending", "ProductCode",Order.Desc),
            SortItem("Barcode Ascending", "Barcode",Order.Asc),
            SortItem("Barcode Descending", "Barcode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val order: String = Order.Asc.value,
        val page: Int = 1,
        val showSortList: Boolean = false,
        val showConfirm: Boolean = false,
        val error: String = "",
        val lockKeyboard: Boolean = false,
        val total: Double = 0.0,
        val scan: Double = 0.0
    ) : UiState

    sealed class Event : UiEvent {
        data object CloseError: Event()
        data object OnNavBack : Event()
        data object OnClearBarcode: Event()
        data object HideToast: Event()
        data class OnSelectDetail(val barcode: String?) : Event()
        data class OnSelectSort(val sort: SortItem) : Event()
        data class OnSelectOrder(val order: String) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data class OnDetailClick(val detail: ReceivingDetailRow) : Event()
        data object FetchData : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
        data class OnNavToInception(val detail: ReceivingDetailRow): Effect()
    }
}