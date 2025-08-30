package com.linari.presentation.shipping.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.auth.models.WarehouseModel
import com.linari.data.checking.models.PalletStatusRow
import com.linari.data.shipping.models.CustomerPalletIsNotInShippingRow
import com.linari.data.shipping.models.ShippingCustomerRow
import com.linari.data.shipping.models.DriverModel
import com.linari.data.shipping.models.PalletInShippingRow
import com.linari.data.shipping.models.PalletTypeRow
import com.linari.data.shipping.models.ShippingDetailListOfPalletRow
import com.linari.data.shipping.models.ShippingModel
import com.linari.data.shipping.models.ShippingPalletManifestRow
import com.linari.data.shipping.models.ShippingRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class ShippingContract {
    data class State(
        //create shipping
        val driverId: TextFieldValue = TextFieldValue(),
        val driverName: TextFieldValue = TextFieldValue(),
        val carNumber: TextFieldValue = TextFieldValue(),
        val trailerNumber : TextFieldValue = TextFieldValue(),
        val palletNumber: TextFieldValue = TextFieldValue(),
        val createPallets: List<ShippingPalletManifestRow> = emptyList(),
        val shippingPalletManifestList: List<ShippingPalletManifestRow> = emptyList(),
        val palletProducts: List<ShippingDetailListOfPalletRow> = emptyList(),
        val showAddDialog: Boolean = false,
        val selectedDriver: DriverModel? = null,
        val isDriverIdScanned: Boolean = false,
        val selectedPallet: ShippingPalletManifestRow? = null,
        //pallet quantity
        val quantity: TextFieldValue = TextFieldValue(),
        val customer: TextFieldValue = TextFieldValue(),
        val palletType: TextFieldValue = TextFieldValue(),
        val palletStatus: TextFieldValue = TextFieldValue(),
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
        ),
        val sort: SortItem = sortList.first(),
        val showFilterList: Boolean = false,
        val confirmShipping: ShippingRow? = null,
        val invoiceShipping: ShippingRow? = null,
        val rsShipping: ShippingRow? = null,
        val showPopup: Boolean = false,
        val lockKeyboard: Boolean = false,
        val customers: List<ShippingCustomerRow> = emptyList(),
        val palletTypes: List<PalletTypeRow> = emptyList(),
        val selectedCustomer: ShippingCustomerRow? = null,
        val selectedPalletType: PalletTypeRow? = null,
        val isChecking: Boolean = false,
        val isShipping: Boolean = false,
        val isCreatingPallet: Boolean = false,
        val isAddingPallet: Boolean = false,
        val isUpdatingPallet: Boolean = false,
        val isDeletingPallet: Boolean = false,
        val isConfirming: Boolean = false,
        val isCreatingInvoice: Boolean = false,
        val isCreatingRs: Boolean = false,
        val isProductLoading: Boolean = false,
        val palletStatusList: List<PalletStatusRow> = emptyList(),
        val selectedPalletStatus: PalletStatusRow? = null,
        val editDriver: Boolean = false,
        val showAddPallet: Boolean = false,
        val palletMask: String = "",
        val warehouseID: String = "",
        val showUpdatePallet: PalletInShippingRow? = null,
        val showConfirmDeletePallet: PalletInShippingRow? = null,
        val showRollbackConfirm: ShippingRow? = null,
        val isRollingBack: Boolean = false,
        val rowCount: Int = 0,
        val showStatusList: Boolean = false,
        val showTypeList: Boolean = false,
        val showCustomerList: Boolean = false,
        val palletNotInShipping: List<CustomerPalletIsNotInShippingRow> = emptyList(),
        val showConfirmOfPalletConfirm: ShippingRow? = null,
        val warehouse: WarehouseModel? = null
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
        data class OnSelectShipping(val shipping: ShippingRow) : Event()
        data class OnConfirm(val shipping: ShippingRow): Event()
        data class OnCreateInvoice(val shipping: ShippingRow): Event()
        data class OnRollbackShipping(val shipping: ShippingRow): Event()
        data class OnShowPalletQuantitySheet(val shipping: ShippingRow?): Event()
        data class OnShowConfirm(val shipping: ShippingRow?) : Event()
        data class OnShowInvoice(val shipping: ShippingRow?) : Event()
        data class OnShowRollbackConfirm(val shipping: ShippingRow?) : Event()
        //create shipping
        data class OnDriverIdChange(val id: TextFieldValue) : Event()
        data class OnDriverNameChange(val name: TextFieldValue) : Event()
        data class OnCarNumberChange(val number: TextFieldValue) : Event()
        data class OnTrailerNumberChange(val number: TextFieldValue) : Event()
        data class OnPalletNumberChange(val number: TextFieldValue) : Event()
        data object OnAddShipping: Event()
        data class OnSelectPallet(val pallet: ShippingPalletManifestRow?) : Event()
        data object OnScanPalletBarcode: Event()
        data class FetchPalletProducts(val palletManifest: ShippingPalletManifestRow) : Event()
        data object OnScanDriverId: Event()
        data class OnRemovePallet(val pallet: ShippingPalletManifestRow) : Event()
        //pallet quantity
        data class OnQuantityChange(val quantity: TextFieldValue) : Event()
        data class OnCustomerChange(val customer: TextFieldValue) : Event()
        data class OnPalletTypeChange(val type: TextFieldValue) : Event()
        data class OnConfirmPallet(val shipping: ShippingRow): Event()
        data object OnScanPalletQuantity: Event()
        data class OnRemovePalletQuantity(val pallet: PalletInShippingRow) : Event()
        data class OnSelectCustomer(val customer: ShippingCustomerRow?) : Event()
        data class OnSelectPalletType(val type: PalletTypeRow?) : Event()
        data class OnSelectPalletStatus(val status: PalletStatusRow?) : Event()
        data class OnPalletStatusChange(val status: TextFieldValue) : Event()
        data class OnShowAddPallet(val show: Boolean) : Event()
        data class OnShowUpdatePallet(val show: PalletInShippingRow?) : Event()

        data class OnShowConfirmDeletePallet(val show: PalletInShippingRow?) : Event()
        data class OnUpdatePallet(val pallet: PalletInShippingRow) : Event()
        data class OnShowStatusList(val show: Boolean ): Event()
        data class OnShowTypeList(val show: Boolean) : Event()

        data class OnShowCustomerList(val show: Boolean) : Event()
        data class CheckHasPallet(val shipping: ShippingRow) : Event()
        data class ShowConfirmOfPalletConfirm(val shipping: ShippingRow?) : Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
        data class NavToShippingDetail(val shipping: ShippingRow) : Effect()
    }
}
