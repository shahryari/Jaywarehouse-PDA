package com.linari.presentation.cycle_count.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.linari.data.cycle_count.models.CycleModel
import com.linari.data.cycle_count.models.CycleRow
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.common.utils.UiEvent
import com.linari.presentation.common.utils.UiSideEffect
import com.linari.presentation.common.utils.UiState

class CycleCountContract {
    data class State(
        val cycleModel: CycleModel? = null,
        val cycleList: List<CycleRow> = emptyList(),
        val cycleCount: Int = 0,
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Created On closed to now", "CreatedOn",Order.Desc),
            SortItem("Created On farthest from now", "CreatedOn",Order.Asc),
            SortItem("Location A-Z","WarehouseLocationCode",Order.Asc),
            SortItem("Location Z-A","WarehouseLocationCode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavToCycleCountDetail(val item: CycleRow) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data class OnSearch(val keyword: String): Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()

    }

    sealed class Effect: UiSideEffect {
        data class NavToCycleCountDetail(val item: CycleRow) : Effect()
        data object NavBack: Effect()
    }
}