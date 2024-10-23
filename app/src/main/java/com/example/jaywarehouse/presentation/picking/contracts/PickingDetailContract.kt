package com.example.jaywarehouse.presentation.picking.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.data.picking.models.PickedScanItemRow
import com.example.jaywarehouse.data.picking.models.PickedScanItemsModel
import com.example.jaywarehouse.data.picking.models.ReadyToPickRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PickingDetailContract {
    data class State(
        val pickingRow: ReadyToPickRow? = null,
        val page: Int = 1,
        val showDetail: Boolean = false,
        val pickings: List<PickedScanItemRow> = emptyList(),
        val customer: CustomerToPickRow? = null,
        val pickedScanList: PickedScanItemsModel? = null,
        val barcode: TextFieldValue = TextFieldValue(),
        val location: TextFieldValue = TextFieldValue(),
        val error: String = "",
        val showFinishDialog: Boolean = false,
        val loadingState: Loading = Loading.NONE,
        val isScanning: Boolean = false,
        val enableLocation: Boolean = true,
        val selectedItem: Int? = null,
        val navigateToParent: Boolean =false,
        val toast: String = "",
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnBarcodeChanged(val barcode: TextFieldValue) : Event()
        data class OnLocationChanged(val location: TextFieldValue) : Event()
        data object OnNavBack : Event()
        data object ClearError : Event()
        data object HideToast : Event()
        data class OnRemovePickedItem(val pickingScanId: Int) : Event()
        data object OnScan: Event()
        data object OnCheckLocation: Event()
        data class OnShowDetailChange(val show: Boolean) : Event()
        data class OnSelectPickedItem(val pickingScanId: Int?) : Event()
        data object OnReachToEnd: Event()
        data object OnRefresh: Event()
        data object HideFinish: Event()
    }

    sealed class Effect : UiSideEffect {
        data object NavigateBack : Effect()
        data object NavigateToParent : Effect()
    }
}