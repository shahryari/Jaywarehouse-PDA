package com.example.jaywarehouse.presentation.putaway.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.putaway.model.PutawaysModel
import com.example.jaywarehouse.data.putaway.model.PutawaysRow
import com.example.jaywarehouse.data.putaway.model.ReadyToPutRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PutawayDetailContract {
    data class State(
        val putRow: ReadyToPutRow? = null,
        val details: PutawaysModel? = null,
        val putaways: List<PutawaysRow> = emptyList(),
        val enableLocation: Boolean = true,
        val enableBoxNumber: Boolean = true,
        val boxNumber: TextFieldValue = TextFieldValue(),
        val location: TextFieldValue = TextFieldValue(),
        val barcode: TextFieldValue = TextFieldValue(),
        val isScanning: Boolean = false,
        val showHeaderDetail: Boolean = false,
        val loadingState: Loading = Loading.NONE,
        val selectedPutaway: Int? = null,
        val showFinishAlertDialog: Boolean = false,
        val error: String = "",
        val page: Int = 1,
        val toast: String = "",
        val lockKeyboard: Boolean = false,
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeBarcode(val barcode: TextFieldValue) : Event()
        data class OnChangeLocation(val location: TextFieldValue) : Event()
        data class OnChangeBoxNumber(val boxNumber: TextFieldValue) : Event()
        data object CheckLocation : Event()
        data object CheckBoxNumber : Event()
        data object ScanBarcode: Event()
        data class OnRemovePut(val putawayScanId: Int) : Event()
        data class OnSelectPut(val putawayScanId: Int?) : Event()
        data object OnNavBack : Event()
        data object CloseError: Event()
        data object HideToast: Event()
        data object HideFinishDialog: Event()
        data object OnReachEnd: Event()
        data object OnRefresh: Event()
        data class OnShowHeaderDetail(val show: Boolean): Event()
    }

    sealed class Effect: UiSideEffect {
        data object NavBack : Effect()
        data object NavToDashboard: Effect()
        data object MoveFocus: Effect()
    }
}