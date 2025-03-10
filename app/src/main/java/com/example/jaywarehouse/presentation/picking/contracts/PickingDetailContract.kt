package com.example.jaywarehouse.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.picking.models.PickingListGroupedRow
import com.example.jaywarehouse.data.picking.models.PickingListModel
import com.example.jaywarehouse.data.picking.models.PickingListRow
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.data.putaway.model.PutawayListModel
import com.example.jaywarehouse.data.putaway.model.PutawayListRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PickingDetailContract {
    data class State(
        val pickRow: PickingListGroupedRow? = null,
        val pickDetailModel: PickingListModel? = null,
        val pickingList: List<PickingListRow> = emptyList(),
        val location: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val selectedPick: PickingListRow? = null,
        val showSortList: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val keyword: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z","ProductName", Order.Asc),
            SortItem("Product Name Z-A","ProductName", Order.Desc),
            SortItem("Product Code A-Z","ProductCode", Order.Asc),
            SortItem("Product Code Z-A","ProductCode", Order.Desc),
            SortItem("Barcode Ascending","Barcode", Order.Asc),
            SortItem("Barcode Descending","Barcode", Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnChangeLocation(val location: TextFieldValue) : Event()
        data class OnSelectPick(val put: PickingListRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnCompletePick(val pick: PickingListRow): Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnSearch(val keyword: String): Event()
        data class OnSortChange(val sortItem: SortItem): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}