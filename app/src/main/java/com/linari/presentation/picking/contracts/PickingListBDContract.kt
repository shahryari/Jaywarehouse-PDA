package com.linari.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.picking.models.PurchaseOrderDetailListBDRow
import com.linari.data.picking.models.PurchaseOrderListBDRow
import com.linari.data.picking.models.PickingListBDRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class PickingListBDContract {
    data class State(
        val shippingOrderDetailList: List<PickingListBDRow> = emptyList(),
        val purchaseOrderRow: PurchaseOrderListBDRow? = null,
        val purchaseOrderDetailRow: PurchaseOrderDetailListBDRow? = null,
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Product Name A-Z", "ProductName",Order.Asc),
            SortItem("Product Name Z-A", "ProductName",Order.Desc),
            SortItem("Product Code Ascending", "ProductCode",Order.Asc),
            SortItem("Product Code Descending", "ProductCode",Order.Desc),
            SortItem("Barcode Ascending", "Barcode",Order.Asc),
            SortItem("Barcode Descending", "Barcode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        val showConfirmFinish: Boolean = false,
        val isFinishing: Boolean = false,
        val selectedPicking: PickingListBDRow? = null,
        val isModifying: Boolean = false,
        val toast: String = "",
        val quantity: TextFieldValue = TextFieldValue(),
        val selectedForWaste: PickingListBDRow? = null,
        val isWasting: Boolean = false,
        val hasModify: Boolean = true,
        val hasWaste: Boolean = true,
    ) : UiState

    sealed class Event : UiEvent {
//        data class OnPurchaseDetailClick(val purchase: PurchaseOrderDetailListBDRow) : Event()
        data object ClearError: Event()
        data object HideToast: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()
        data class OnShowFinishConfirm(val show: Boolean) : Event()
        data object OnFinish: Event()
        data class OnSelectShippingDetail(val picking: PickingListBDRow?) : Event()
        data object OnModify : Event()
        data class OnQuantityChange(val quantity: TextFieldValue) : Event()
        data class OnSelectForWaste(val picking: PickingListBDRow?) : Event()
        data class OnWaste(val picking: PickingListBDRow) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
    }
}