package com.example.jaywarehouse.presentation.picking.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.picking.PickingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.picking.contracts.PickingCustomerContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PickingCustomerViewModel(
    private val repository: PickingRepository,
    private val prefs: Prefs
) : BaseViewModel<PickingCustomerContract.Event, PickingCustomerContract.State, PickingCustomerContract.Effect>() {
    init {
        setState {
            copy(sort = prefs.getPickingCustomerSort(), order = prefs.getPickingCustomerOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): PickingCustomerContract.State {
        return PickingCustomerContract.State()
    }

    override fun onEvent(event: PickingCustomerContract.Event) {
        when(event){
            PickingCustomerContract.Event.OnNavBAck -> {
                setEffect{PickingCustomerContract.Effect.NavigateBack}
            }
            is PickingCustomerContract.Event.OnPickClick -> {
                setEffect {
                    PickingCustomerContract.Effect.NavigateToPicking(event.customerToPickRow)
                }
            }
            is PickingCustomerContract.Event.OnKeyWordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }

            is PickingCustomerContract.Event.OnOrderChanged -> {
                prefs.setPickingCustomerOrder(event.order)
                setState {
                    copy(order = event.order, page = 1, customerToPicks = emptyList(), loadingState = Loading.LOADING)
                }
                getCustomerToPick(
                    keyword = state.keyword.text,
                    order = event.order,
                    sort = state.sort
                )
            }
            is PickingCustomerContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }
            is PickingCustomerContract.Event.OnSortChanged -> {
                prefs.setPickingCustomerSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, customerToPicks = emptyList(), loadingState = Loading.LOADING)
                }
                getCustomerToPick(
                    keyword = state.keyword.text,
                    order = state.order,
                    sort = event.sort
                )
            }

            PickingCustomerContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }

            PickingCustomerContract.Event.OnReachEnd -> {
                if (10*state.page <= state.customerToPicks.size){
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getCustomerToPick(
                        keyword = state.keyword.text,
                        page = state.page,
                        order = state.order,
                        sort = state.sort
                    )
                }
            }

            PickingCustomerContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, customerToPicks = emptyList(), loadingState = Loading.SEARCHING)
                }
                getCustomerToPick(
                    keyword = state.keyword.text,
                    order = state.order,
                    sort = state.sort,
                    isSearching = false
                )
            }

            PickingCustomerContract.Event.OnRefresh -> {
                setState {
                    copy(loadingState = Loading.REFRESHING, page = 1, customerToPicks = emptyList())
                }
                getCustomerToPick(
                    keyword = state.keyword.text,
                    order = state.order,
                    sort = state.sort
                )
            }

            PickingCustomerContract.Event.FetchData -> {
                setState {
                    copy(loadingState = Loading.LOADING, page = 1, customerToPicks = emptyList(), keyword = TextFieldValue())
                }
                getCustomerToPick(
                    keyword = state.keyword.text,
                    order = state.order,
                    sort = state.sort
                )
            }
        }
    }

    private fun getCustomerToPick(
        keyword: String = "",
        page: Int = 1,
        order: String = Order.Asc.value,
        sort: String = "CreatedOn",
        isSearching: Boolean = false,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerToPick(
                keyword,
                page,
                10,
                sort = sort,
                order = order
            ).catch {
                setState {
                    copy(error = it.message.toString(), loadingState = Loading.NONE)
                }
            }.collect {
                setSuspendedState {
                    copy(
                        loadingState = Loading.NONE
                    )
                }
                when(it){
                    is BaseResult.Error -> {
                        setState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setState {
                            copy(customerToPick = it.data,customerToPicks = customerToPicks + (it.data?.rows ?: emptyList()))
                        }
                        if (state.customerToPicks.size == 1 && prefs.getIsNavToDetail() && isSearching){
                            setEffect {
                                PickingCustomerContract.Effect.NavigateToPicking(state.customerToPicks[0])
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}