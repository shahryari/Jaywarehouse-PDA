package com.example.jaywarehouse.presentation.checking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.checking.models.CheckingListModel
import com.example.jaywarehouse.data.checking.models.CheckingListRow
import com.example.jaywarehouse.data.checking.models.PalletStatusModel
import com.example.jaywarehouse.data.checking.models.PalletStatusRow
import com.example.jaywarehouse.data.shipping.models.PalletTypeRow
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
        val keyword: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Reference Number Ascending","ReferenceNumber", Order.Asc),
            SortItem("Reference Number Descending","ReferenceNumber", Order.Desc),
            SortItem("Product Name A-Z","ProductName", Order.Asc),
            SortItem("Product Name Z-A","ProductName", Order.Desc),
            SortItem("Product Code Ascending","ProductCode", Order.Asc),
            SortItem("Product Code Descending","ProductCode", Order.Desc),
            SortItem("Barcode Ascending","BarcodeNumber", Order.Asc),
            SortItem("Barcode Descending","BarcodeNumber", Order.Desc),
        ),
        val sort: SortItem = sortList.first(),
        val palletTypeList: List<PalletTypeRow> = emptyList(),
        val palletStatusList: List<PalletStatusRow> = emptyList(),
        val selectedPalletType: PalletTypeRow? = null,
        val selectedPalletStatus: PalletStatusRow? = null,
        val palletType: TextFieldValue = TextFieldValue(),
        val palletStatus: TextFieldValue = TextFieldValue(),
        val onSaving: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnChangeLocation(val location: TextFieldValue) : Event()
        data class OnSelectCheck(val checking: CheckingListRow?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnCompleteChecking(val checking: CheckingListRow): Event()
        data class OnShowSortList(val show: Boolean) : Event()
        data class OnSearch(val keyword: String): Event()
        data class OnSortChange(val sortItem: SortItem): Event()
        data class OnSelectPalletType(val palletType: PalletTypeRow?) : Event()
        data class OnSelectPalletStatus(val palletStatus: PalletStatusRow?) : Event()
        data class OnPalletTypeChange(val palletType: TextFieldValue) : Event()
        data class OnPalletStatusChange(val palletStatus: TextFieldValue) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}