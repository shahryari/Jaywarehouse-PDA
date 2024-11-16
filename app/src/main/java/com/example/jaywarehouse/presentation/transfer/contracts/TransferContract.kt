package com.example.jaywarehouse.presentation.transfer.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.putaway.model.PutawayListGroupedRow
import com.example.jaywarehouse.data.transfer.models.ProductStatusRow
import com.example.jaywarehouse.data.transfer.models.TransferModel
import com.example.jaywarehouse.data.transfer.models.TransferRow
import com.example.jaywarehouse.data.transfer.models.WarehouseLocationRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class TransferContract {
    data class State(
        val transferModel: TransferModel? = null,
        val transferList: List<TransferRow> = emptyList(),
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val toast: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn",Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Receiving Number Descending", "Receiving",Order.Desc),
            SortItem("Receiving Number Ascending", "Receiving",Order.Asc),
            SortItem("Most Progress", "Progress",Order.Desc),
            SortItem("Least Progress", "Progress",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val selectedTransfer: TransferRow? = null,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        //transfer
        val destination: TextFieldValue = TextFieldValue(),
        val productStatus: TextFieldValue = TextFieldValue(),
        val quantity: TextFieldValue = TextFieldValue(),
        val expirationDate: TextFieldValue = TextFieldValue(),
        val showDatePicker: Boolean = false,
        val selectedProductStatus: ProductStatusRow? = null,
        val productStatusList: List<ProductStatusRow> = emptyList(),
        val selectedLocation: WarehouseLocationRow? = null,
        val locationList: List<WarehouseLocationRow> = emptyList(),
        val isSaving: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data object ClearError: Event()
        data object HideToast: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()
        data class OnSelectTransfer(val transferRow: TransferRow?) : Event()
        //transfer
        data class OnChangeDestination(val destination: TextFieldValue) : Event()
        data class OnChangeProductStatus(val productStatus: TextFieldValue) : Event()
        data class OnChangeQuantity(val quantity: TextFieldValue) : Event()
        data class OnChangeExpirationDate(val expirationDate: TextFieldValue) : Event()
        data class OnTransfer(val row: TransferRow) : Event()
        data class OnShowDatePicker(val show: Boolean) : Event()
        data class OnSelectProductStatus(val productStatus: ProductStatusRow?) : Event()
        data class OnSelectWarehouseLocation(val location: WarehouseLocationRow?) : Event()

    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
    }
}