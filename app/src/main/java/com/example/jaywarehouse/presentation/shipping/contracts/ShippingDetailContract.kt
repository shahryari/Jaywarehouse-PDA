package com.example.jaywarehouse.presentation.shipping.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.pallet.model.PalletManifestProductRow
import com.example.jaywarehouse.data.picking.models.PalletManifest
import com.example.jaywarehouse.data.shipping.models.ShippingDetailListOfPalletRow
import com.example.jaywarehouse.data.shipping.models.ShippingPalletManifestRow
import com.example.jaywarehouse.data.shipping.models.ShippingRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class ShippingDetailContract {
    data class State(
        val barcode: TextFieldValue = TextFieldValue(),
        val palletList: List<PalletManifest> = emptyList(),
        val shipping: ShippingRow? = null,
        val isScanning: Boolean = false,
        val sortList: List<SortItem> = listOf(
            SortItem("Pallet Barcode Ascending","PalletBarcode", order = Order.Asc),
            SortItem("Pallet Barcode Descending","PalletBarcode",order = Order.Desc),
        ),
        val sort: SortItem = sortList.first(),
        val keyword: String = "",
        val selectedPallet: PalletManifest? = null,
        val productList: List<PalletManifestProductRow> = emptyList(),
        val loadingState: Loading = Loading.NONE,
        val page: Int = 1,
        val productPage: Int = 1,
        val isProductLoading: Boolean = false,
        val error: String = "",
        val toast: String = "",
        val selectedForDelete: PalletManifest? = null,
        val isDeleting: Boolean = false,
        val lockKeyboard: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data object FetchPalletData :Event()
        data class OnSearch(val keyword: String) : Event()
        data object OnRefresh: Event()
        data object OnReachEnd: Event()
        data object OnProductReachEnd: Event()
        data class OnChangeSort(val sort: SortItem): Event()
        data class OnSelectPallet(val pallet: PalletManifest?) : Event()
        data class FetchPalletProducts(val pallet: PalletManifest) : Event()
        data object OnScan: Event()
        data class OnSelectForDelete(val pallet: PalletManifest?) : Event()
        data class OnDelete(val pallet: PalletManifest) : Event()
        data object OnNavBack: Event()
        data object CloseError: Event()
        data object CloseToast: Event()
    }


    sealed class Effect : UiSideEffect {
        data object NavBack: Effect()
    }
}