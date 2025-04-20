package com.example.jaywarehouse.presentation.shipping.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.shipping.models.CustomerRow
import com.example.jaywarehouse.data.shipping.models.DriverModel
import com.example.jaywarehouse.data.shipping.models.PalletInShippingRow
import com.example.jaywarehouse.data.shipping.models.PalletTypeRow
import com.example.jaywarehouse.data.shipping.models.ShippingModel
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ShippingContract {
    data class State(
        //create shipping
        val driverId: TextFieldValue = TextFieldValue(),
        val driverName: TextFieldValue = TextFieldValue(),
        val carNumber: TextFieldValue = TextFieldValue(),
        val trailerNumber : TextFieldValue = TextFieldValue(),
        val palletNumber: TextFieldValue = TextFieldValue(),
        val createPallets: List<PalletConfirmRow> = emptyList(),
        val showAddDialog: Boolean = false,
        val selectedDriver: DriverModel? = null,
        val isDriverIdScanned: Boolean = false,
        //pallet quantity
        val quantity: TextFieldValue = TextFieldValue(),
        val customer: TextFieldValue = TextFieldValue(),
        val palletType: TextFieldValue = TextFieldValue(),
        val quantityPallets: List<PalletInShippingRow> = emptyList(),
        val shippingForPallet: ShippingRow? = null,
        //
        val keyword: String = "",
        val page: Int = 1,
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val toast: String = "",
        val shippingModel: ShippingModel? = null,
        val shippingList: List<ShippingRow> = emptyList(),
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn",Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Receiving Number Descending", "Receiving",Order.Desc),
            SortItem("Receiving Number Ascending", "Receiving",Order.Asc),
            SortItem("Most Progress", "Progress",Order.Desc),
            SortItem("Least Progress", "Progress",Order.Asc)
        ),
        val sort: SortItem = sortList.first(),
        val showFilterList: Boolean = false,
        val confirmShipping: ShippingRow? = null,
        val invoiceShipping: ShippingRow? = null,
        val rsShipping: ShippingRow? = null,
        val showPopup: Boolean = false,
        val lockKeyboard: Boolean = false,
        val customers: List<CustomerRow> = emptyList(),
        val palletTypes: List<PalletTypeRow> = emptyList(),
        val selectedCustomer: CustomerRow? = null,
        val selectedPalletType: PalletTypeRow? = null,
        val isChecking: Boolean = false,
        val isShipping: Boolean = false,
        val isCreatingPallet: Boolean = false,
        val isConfirming: Boolean = false,
        val isCreatingInvoice: Boolean = false,
        val isCreatingRs: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnSortChange(val sort: SortItem) : Event()
        data class OnShowFilterList(val showFilterList: Boolean) : Event()
        data object OnClearError : Event()
        data class OnShowAddDialog(val showAddDialog: Boolean) : Event()
        data object HideToast : Event()
        data class OnShowPopup(val show: Boolean) : Event()
        data object OnReachEnd : Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object FetchData: Event()
        data object OnNavBack : Event()
        data class OnConfirm(val shipping: ShippingRow): Event()
        data class OnCreateInvoice(val shipping: ShippingRow): Event()
        data class OnCreateRS(val shipping: ShippingRow): Event()
        data class OnShowPalletQuantitySheet(val shipping: ShippingRow?): Event()
        data class OnShowConfirm(val shipping: ShippingRow?) : Event()
        data class OnShowInvoice(val shipping: ShippingRow?) : Event()
        data class OnShowRs(val shipping: ShippingRow?) : Event()
        //create shipping
        data class OnDriverIdChange(val id: TextFieldValue) : Event()
        data class OnDriverNameChange(val name: TextFieldValue) : Event()
        data class OnCarNumberChange(val number: TextFieldValue) : Event()
        data class OnTrailerNumberChange(val number: TextFieldValue) : Event()
        data class OnPalletNumberChange(val number: TextFieldValue) : Event()
        data object OnAddShipping: Event()
        data object OnScanPalletBarcode: Event()
        data object OnScanDriverId: Event()
        data class OnRemovePallet(val pallet: PalletConfirmRow) : Event()
        //pallet quantity
        data class OnQuantityChange(val quantity: TextFieldValue) : Event()
        data class OnCustomerChange(val customer: TextFieldValue) : Event()
        data class OnPalletTypeChange(val type: TextFieldValue) : Event()
        data object OnAddPallet: Event()
        data object OnScanPalletQuantity: Event()
        data class OnRemovePalletQuantity(val pallet: PalletInShippingRow) : Event()
        data class OnSelectCustomer(val customer: CustomerRow) : Event()
        data class OnSelectPalletType(val type: PalletTypeRow) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}
