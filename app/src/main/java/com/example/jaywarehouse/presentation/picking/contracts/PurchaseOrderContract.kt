package com.example.jaywarehouse.presentation.picking.contracts

import com.example.jaywarehouse.data.picking.models.PickingListGroupedModel
import com.example.jaywarehouse.data.picking.models.PickingListGroupedRow
import com.example.jaywarehouse.data.picking.models.PurchaseOrderListBDRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PurchaseOrderContract {
    data class State(
        val purchaseOrderList: List<PurchaseOrderListBDRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Supplier Name A-Z","SupplierName", Order.Asc),
            SortItem("Supplier Name Z-A","SupplierName", Order.Desc),
            SortItem("Supplier Code A-Z","SupplierCode", Order.Asc),
            SortItem("Supplier Code Z-A","SupplierCode", Order.Desc),
            SortItem("Reference Number Ascending","ReferenceNumber",Order.Asc),
            SortItem("Reference Number Descending","ReferenceNumber",Order.Desc),
            SortItem("Purchase Order Date furthest from now","PurchaseOrderDate",Order.Asc),
            SortItem("Purchase Order Date closed to now","PurchaseOrderDate",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnPurchaseClick(val purchase: PurchaseOrderListBDRow) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()

    }

    sealed class Effect: UiSideEffect {
        data class NavToPurchaseOrderDetail(val purchase: PurchaseOrderListBDRow) : Effect()
        data object NavBack: Effect()
    }
}