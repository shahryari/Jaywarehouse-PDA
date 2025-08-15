package com.linari.presentation.checking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.checking.models.CheckingListGroupedRow
import com.linari.data.checking.models.CheckingListModel
import com.linari.data.checking.models.CheckingListRow
import com.linari.data.checking.models.PalletStatusModel
import com.linari.data.checking.models.PalletStatusRow
import com.linari.data.shipping.models.PalletTypeRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

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
        val statusLock: Boolean = false,
        val typeLock: Boolean = false,
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
        val showTypeList: Boolean = false,
        val showStatusList: Boolean = false,
        val onSaving: Boolean = false,
        val palletMask: String = "",
        val isDamaged: Boolean = false,
        val cancelQuantity: TextFieldValue = TextFieldValue(),
        val cancelLocation: TextFieldValue = TextFieldValue(),
        val selectedForCancel: CheckingListRow? = null,
        val isCanceling: Boolean = false,
        val rowCount: Int = 0,
        val locationBase: Boolean = true,
        val hasPickCancel: Boolean = false,
        val enableTransferOnPickCancel: Boolean = false,
        val onPickCancelLocationCode: String = "",
        val warehouse: WarehouseModel? = null
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

        data class ShowTypeList(val show: Boolean) : Event()
        data class ShowStatusList(val show: Boolean) : Event()
        data class SelectForCancel(val checking: CheckingListRow?) : Event()
        data class OnChangeCancelQuantity(val value: TextFieldValue) : Event()
        data class OnChangeCancelLocation(val value: TextFieldValue) : Event()
        data class OnCancelChecking(val checking: CheckingListRow) : Event()
        data class OnChangeIsDamaged(val isDamaged: Boolean) : Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
    }
}