package com.example.jaywarehouse.presentation.checking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.checking.models.CheckingListRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CheckingDetailContract {
    data class State(
        val checkRow: CheckingListGroupedRow? = null,
        val checkingDetail: CheckingListModel? = null,
        val checkingList: List<CheckingListRow> = emptyList(),
        val count: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val selectedChecking: CheckingListRow? = null,
        val showSortList: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
        val keyword: TextFieldValue = TextFieldValue(),
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn", Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Receiving Number Descending", "Receiving",Order.Desc),
            SortItem("Receiving Number Ascending", "Receiving",Order.Asc),
            SortItem("Most Progress", "Progress",Order.Desc),
            SortItem("Least Progress", "Progress",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val onSaving: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnChangeLocation(val location: TextFieldValue) : Event()
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnSelectCheck(val checking: CheckingListRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnCompleteChecking(val checking: CheckingListRow): Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object OnSearch: Event()
        data class OnSortChange(val sortItem: SortItem): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}