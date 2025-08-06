package com.linari.presentation.rs.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.rs.models.PODInvoiceRow
import com.linari.data.shipping.models.DriverModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class RSIntegrationContract {


    data class State(
        val keyword: String =  "",
        val rsList: List<PODInvoiceRow> = emptyList(),
        val error: String = "",
        val loadingState: Loading = Loading.NONE,
        val showSortList: Boolean = false,
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn", Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn", Order.Asc),
            SortItem("Receiving Number Descending", "Receiving", Order.Desc),
            SortItem("Receiving Number Ascending", "Receiving", Order.Asc),
            SortItem("Most Progress", "Progress", Order.Desc),
            SortItem("Least Progress", "Progress", Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val lockKeyboard: Boolean = false,
        val selectedRs: PODInvoiceRow? = null,
        //update bottom sheet
        val driver: TextFieldValue = TextFieldValue(),
        val driverTin: TextFieldValue = TextFieldValue(),
        val carNumber: TextFieldValue = TextFieldValue(),
        val trailer: TextFieldValue = TextFieldValue(),
        val isDriverScanned: Boolean = false,
        val selectedDriver: DriverModel? = null,
        val isSubmitting: Boolean = false,
        val rowCount: Int = 0
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSearch(val keyword: String) : Event()
        data object OnNavBack : Event()
        data object OnRefresh : Event()
        data object FetchData : Event()
        data object OnReachEnd: Event()
        data class OnSortChange(val sort: SortItem) : Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data object CloseError: Event()
        data class OnSelectRs(val rs: PODInvoiceRow?) : Event()
        //update bottom sheet
        data class OnDriverChange(val driver: TextFieldValue) : Event()
        data class OnDriverTinChange(val driverTin: TextFieldValue) : Event()
        data class OnCarNumberChange(val carNumber: TextFieldValue) : Event()
        data class OnTrailerChange(val trailer: TextFieldValue) : Event()
        data class OnSubmit(val rs: PODInvoiceRow): Event()
        data object OnScanDriverTin: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack : Effect()
    }
}