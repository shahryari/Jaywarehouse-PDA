package com.linari.presentation.putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.data.putaway.model.PutawayListModel
import com.linari.data.putaway.model.PutawayListRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class PutawayDetailContract {
    data class State(
        val putRow: PutawayListGroupedRow? = null,
        val details: PutawayListModel? = null,
        val putaways: List<PutawayListRow> = emptyList(),
//        val enableLocation: Boolean = true,
//        val enableBoxNumber: Boolean = true,
//        val boxNumber: TextFieldValue = TextFieldValue(),
        val location: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
//        val isScanning: Boolean = false,
//        val showHeaderDetail: Boolean = false,
        val loadingState: Loading = Loading.NONE,
        val selectedPutaway: PutawayListRow? = null,
//        val showFinishAlertDialog: Boolean = false,
        val showSortList: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val keyword: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName", Order.Asc),
            SortItem("Product Name Z-A", "ProductName",Order.Desc),
            SortItem("Product Code Ascending", "ProductCode",Order.Asc),
            SortItem("Product Code Descending", "ProductCode",Order.Desc),
            SortItem("Barcode Ascending", "Barcode",Order.Asc),
            SortItem("Barcode Descending", "Barcode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnChangeLocation(val location: TextFieldValue) : Event()
//        data class OnChangeBoxNumber(val boxNumber: TextFieldValue) : Event()
//        data object CheckLocation : Event()
//        data object CheckBoxNumber : Event()
//        data object ScanBarcode: Event()
//        data class OnRemovePut(val putawayScanId: Int) : Event()
        data class OnSelectPut(val put: PutawayListRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
//        data object HideFinishDialog: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
//        data class OnShowHeaderDetail(val show: Boolean): Event()
        data class OnSavePutaway(val putaway: PutawayListRow): Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnSearch(val keyword: String): Event()
        data class OnSortChange(val sortItem: SortItem): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}