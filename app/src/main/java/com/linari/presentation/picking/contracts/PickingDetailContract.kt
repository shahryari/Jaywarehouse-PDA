package com.linari.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.picking.models.PickingListGroupedRow
import com.linari.data.picking.models.PickingListModel
import com.linari.data.picking.models.PickingListRow
import com.linari.data.putaway.model.PutawayListGroupedRow
import com.linari.data.putaway.model.PutawayListModel
import com.linari.data.putaway.model.PutawayListRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

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
            SortItem("Barcode Descending","Barcode", Order.Desc),
            SortItem("Reference Number Ascending","ReferenceNumber",Order.Asc),
            SortItem("Reference Number Descending","ReferenceNumber",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
        val showModify: PickingListRow? =null,
        val showWaste: PickingListRow? = null,
        val quantity: TextFieldValue = TextFieldValue(),
        val isWasting: Boolean = false,
        val isModifying: Boolean = false,
        val hasModify: Boolean = true,
        val hasWaste: Boolean = true,
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
        data class OnShowModify(val pick: PickingListRow?): Event()
        data class OnShowWaste(val pick: PickingListRow?): Event()
        data class OnWastePick(val pick: PickingListRow): Event()
        data class OnModifyPick(val pick: PickingListRow): Event()
        data class ChangeQuantity(val quantity: TextFieldValue): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}