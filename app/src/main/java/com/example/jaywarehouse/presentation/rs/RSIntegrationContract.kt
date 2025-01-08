package com.example.jaywarehouse.presentation.rs

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.rs.models.PODInvoiceRow
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

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