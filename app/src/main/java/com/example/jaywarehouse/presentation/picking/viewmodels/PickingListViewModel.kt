package com.example.jaywarehouse.presentation.picking.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.data.picking.models.CustomerToPickRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.picking.contracts.PickingListContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingListViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs,
    private val customerRow: CustomerToPickRow
) : BaseViewModel<PickingListContract.Event, PickingListContract.State, PickingListContract.Effect>() {
    init {
        setState {
            copy(customer = customerRow, sort = prefs.getPickingSort(), order = prefs.getPickingOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): PickingListContract.State {
        return PickingListContract.State()
    }

    override fun onEvent(event: PickingListContract.Event) {
        when(event){
            PickingListContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            PickingListContract.Event.OnNavBack -> {
                setEffect{PickingListContract.Effect.NavigateBack}
            }
            is PickingListContract.Event.OnOrderChanged -> {
                prefs.setPickingOrder(event.order)
                setState {
                    copy(order = event.order,page = 1, pickingList = emptyList(), loadingState = Loading.LOADING)
                }
                getReadyToPick(
                    state.keyword.text,
                    customerRow.customerID,
                    order = event.order,
                    sort = state.sort
                )
            }
            is PickingListContract.Event.OnPickClick -> {
                setEffect {
                    PickingListContract.Effect.NavigateToPickingDetail(event.readyToPickRow,false)
                }
            }
            is PickingListContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is PickingListContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }
            is PickingListContract.Event.OnSortChanged -> {
                prefs.setPickingSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, pickingList = emptyList(), loadingState = Loading.LOADING)
                }
                getReadyToPick(
                    keyword = state.keyword.text,
                    customerId = customerRow.customerID,
                    order = state.order,
                    sort = event.sort
                )
            }
            PickingListContract.Event.OnReachToEnd -> {
                if(10*state.page<=state.pickingList.size){
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getReadyToPick(
                        state.keyword.text,
                        customerRow.customerID,
                        order = state.order,
                        sort = state.sort
                    )
                }
            }

            PickingListContract.Event.OnSearch -> {
                setState {
                    copy(
                        page = 1,
                        pickingList = emptyList(),
                        loadingState = Loading.SEARCHING
                    )
                }
                getReadyToPick(
                    keyword = state.keyword.text,
                    customerId = customerRow.customerID,
                    order = state.order,
                    sort = state.sort,
                    isSearching = true
                )
            }

            PickingListContract.Event.OnRefresh -> {
                setState {
                    copy(
                        page = 1,
                        pickingList = emptyList(),
                        loadingState = Loading.REFRESHING
                    )
                }
                getReadyToPick(
                    keyword = state.keyword.text,
                    customerId = customerRow.customerID,
                    order = state.order,
                    sort = state.sort
                )
            }

            PickingListContract.Event.FetchData -> {
                setState {
                    copy(page = 1, pickingList = emptyList(), loadingState = Loading.LOADING, keyword = TextFieldValue())
                }
                getReadyToPick(state.keyword.text,customerRow.customerID,state.page,state.order,state.sort)
            }
        }
    }

    private fun getReadyToPick(
        keyword: String = "",
        customerId: Int,
        page: Int = 1,
        order: String = Order.Asc.value,
        sort: String = "CreatedOn",
        isSearching: Boolean = false,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReadyToPicked(
                keyword = keyword,
                customerID = customerId,
                page = page,
                rows = 10,
                order = order,
                sort = sort
            ).catch {
                setSuspendedState {
                    copy(
                        loadingState = Loading.NONE,
                        error = it.message.toString()
                    )
                }
            }.collect {
                setSuspendedState {
                    copy(loadingState = Loading.NONE)
                }
                when(it){
                    is BaseResult.Error -> {
                        setState {
                            copy(
                                error = it.message
                            )
                        }
                    }
                    is BaseResult.Success -> {
                        setState {
                            copy(
                                pickModel = it.data,
                                pickingList = pickingList + (it.data?.rows?: emptyList())
                            )
                        }
                        if (state.pickingList.size == 1 && prefs.getIsNavToDetail() && isSearching){
                            setEffect {
                                PickingListContract.Effect.NavigateToPickingDetail(state.pickingList.first(),true)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}