package com.example.jaywarehouse.presentation.manual_putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayDetailRow
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState


class ManualPutawayDetailContract {

    data class State(
        val putaway: ManualPutawayRow? = null,
        val loadingState: Loading = Loading.NONE,
        val lockKeyboard: Boolean = false,
        val keyword: String = "",
        val quantity: TextFieldValue = TextFieldValue(),
        val quantityInPacket: TextFieldValue = TextFieldValue(),
        val locationCode: TextFieldValue = TextFieldValue(),
        val count: Int = 0,
        val error: String = "",
        val toast: String = "",
        val details: List<ManualPutawayDetailRow> = emptyList(),
        val page: Int = 1,
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn", Order.Asc),
            SortItem("Created On farthest from now", "CreatedOn", Order.Desc),
            SortItem("Location Code A-Z", "LocationCode", Order.Asc),
            SortItem("Location Code Z-A", "LocationCode", Order.Desc)
        ),
        val selectedSort: SortItem = sortList.first(),
        val showSortList: Boolean = false,
        val isScanning: Boolean = false,
        val isFinishing: Boolean = false,
        val isDeleting: Boolean = false,
        val selectedDetail: ManualPutawayDetailRow? = null,
        val showConfirmFinish: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnQuantityChange(val quantity: TextFieldValue) : Event()
        data class OnQuantityInPacketChange(val quantity: TextFieldValue) : Event()
        data class OnLocationCodeChange(val locationCode: TextFieldValue) : Event()
        data class OnSortChange(val sort: SortItem) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnSelectDetail(val detail: ManualPutawayDetailRow?) : Event()
        data object OnCloseError: Event()
        data object HideToast: Event()
        data object OnSubmit: Event()
        data object OnRefresh: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnReachEnd: Event()
        data object OnNavBack: Event()
        data object OnAddClick: Event()
        data class OnRemove(val detail: ManualPutawayDetailRow) : Event()
        data class OnShowConfirmFinish(val show: Boolean) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}