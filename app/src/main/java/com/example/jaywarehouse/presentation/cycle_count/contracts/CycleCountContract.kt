package com.example.jaywarehouse.presentation.cycle_count.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.cycle_count.models.CycleModel
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class CycleCountContract {
    data class State(
        val cycleModel: CycleModel? = null,
        val cycleList: List<CycleRow> = emptyList(),
        val cycleCount: Int = 0,
        val keyword: TextFieldValue = TextFieldValue(),
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
        data class OnChangeKeyword(val keyword: TextFieldValue) : Event()
        data class OnNavToCycleCountDetail(val item: CycleRow) : Event()
        data object ClearError: Event()
        data class OnChangeSort(val sort: SortItem) : Event()
        data class OnShowSortList(val showSortList: Boolean) : Event()
        data object ReloadScreen: Event()
        data object OnReachedEnd: Event()
        data object OnSearch: Event()
        data object OnRefresh: Event()
        data object OnBackPressed: Event()

    }

    sealed class Effect: UiSideEffect {
        data class NavToCycleCountDetail(val item: CycleRow) : Effect()
        data object NavBack: Effect()
    }
}