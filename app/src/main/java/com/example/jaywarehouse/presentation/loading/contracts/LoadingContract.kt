package com.example.jaywarehouse.presentation.loading.contracts

import androidx.compose.ui.text.input.TextFieldValue
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedModel
import com.example.jaywarehouse.data.checking.models.CheckingListGroupedRow
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedModel
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.common.utils.UiEvent
import com.example.jaywarehouse.presentation.common.utils.UiSideEffect
import com.example.jaywarehouse.presentation.common.utils.UiState

class LoadingContract {
    data class State(
        val loadingModel: LoadingListGroupedModel? = null,
        val loadingList: List<LoadingListGroupedRow> = emptyList(),
        val keyword: String = "",
        val loadingState: Loading = Loading.NONE,
        val error: String = "",
        val sortList: List<SortItem> = listOf(
            SortItem("Customer Name A-Z", "CustomerName", Order.Asc),
            SortItem("Customer Name Z-A", "CustomerName", Order.Desc),
            SortItem("Customer Code Ascending","CustomerCode",Order.Asc),
            SortItem("Customer Code Descending","CustomerCode",Order.Desc)
        ),
        val sort: SortItem = sortList.first(),
        val page: Int = 1,
        val showSortList: Boolean = false,
        val lockKeyboard: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
        data class OnNavToLoadingDetail(val item: LoadingListGroupedRow) : Event()
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
        data class NavToLoadingDetail(val item: LoadingListGroupedRow) : Effect()
        data object NavBack: Effect()
    }
}