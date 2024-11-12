package com.example.jaywarehouse.presentation.pallet

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedModel
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmModel
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.picking.models.PickingListGroupedModel
import com.example.jaywarehouse.data.picking.models.PickingListGroupedRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class PalletConfirmContract {
    data class State(
        val palletModel: PalletConfirmModel? = null,
        val palletList: List<PalletConfirmRow> = emptyList(),
        val keyword: TextFieldValue = TextFieldValue(),
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
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
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false,
        val selectedPallet: PalletConfirmRow? = null,
        val toast: String = ""
    ) : UiState

    sealed class Event : UiEvent {
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnSelectPallet(val pallet: PalletConfirmRow?) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()
        data class ConfirmPallet(val pallet: PalletConfirmRow) : Event()
        data object HideToast: Event()

    }

    sealed class Effect: UiSideEffect {
        data object NavBack: Effect()
    }
}