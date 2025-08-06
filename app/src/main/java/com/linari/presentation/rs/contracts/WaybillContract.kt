package com.linari.presentation.rs.contracts

import com.linari.data.rs.models.WaybillInfoModel
import com.linari.data.rs.models.WaybillInfoRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class WaybillContract {
    data class State(
        val keyword: String = "",
        val page: Int = 1,
        val sortList: List<SortItem> = listOf(
            SortItem("Waybill Number Ascending","WaybillNumber", Order.Asc),
            SortItem("Waybill Number Descending","WaybillNumber", Order.Desc),
            SortItem("Customer Name A-Z","PartnerName",Order.Asc),
            SortItem("Customer Name Z-A","PartnerName",Order.Desc),
            SortItem("Customer Code A-Z","PartnerCode",Order.Asc),
            SortItem("Customer Code Z-A","PartnerCode",Order.Desc),
            SortItem("Driver Name A-Z","DriverFullName",Order.Asc),
            SortItem("Driver Name Z-A","DriverFullName",Order.Desc),
            SortItem("Driver Tin Ascending","DriverTin",Order.Asc),
            SortItem("Driver Tin Descending","DriverTin",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val loadingState: Loading = Loading.NONE,
        val waybillList: List<WaybillInfoRow> = emptyList(),
        val error: String = "",
        val toast: String = "",
        val selectedWaybill: WaybillInfoRow? = null,
        val isIntegrating: Boolean = false,
        val showSortList: Boolean = false,
        val rowCount: Int = 0,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnReachEnd: Event()
        data object FetchData: Event()
        data class ChangeSort(val sort: SortItem): Event()
        data class OnSelectWaybill(val waybill: WaybillInfoRow?): Event()
        data class OnIntegrateWaybill(val waybill: WaybillInfoRow): Event()
        data class OnShowSortList(val show: Boolean): Event()
        data object OnNavBack: Event()
        data object CloseError: Event()
        data object CloseToast: Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
    }
}