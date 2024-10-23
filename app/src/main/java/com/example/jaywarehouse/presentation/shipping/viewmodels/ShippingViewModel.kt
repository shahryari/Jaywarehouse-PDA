package com.example.jaywarehouse.presentation.shipping.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.shipping.ShippingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.shipping.contracts.ShippingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShippingViewModel(
    private val repository: ShippingRepository,
    private val prefs: Prefs,
) : BaseViewModel<ShippingContract.Event, ShippingContract.State, ShippingContract.Effect>() {

    init {
        setState {
            copy(sort = prefs.getShippingSort(), order = prefs.getShippingOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): ShippingContract.State {
        return ShippingContract.State()
    }

    override fun onEvent(event: ShippingContract.Event) {
        when(event){
            ShippingContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            ShippingContract.Event.OnAddClick -> {
                if (state.selectedDriver!=null)ship(state.selectedDriver!!.driverId)
            }
            ShippingContract.Event.OnClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is ShippingContract.Event.OnDriverChange -> {
                setState {
                    copy(selectedDriver = event.driver, showPopup = false)
                }
            }
            is ShippingContract.Event.OnOrderChange -> {
                prefs.setShippingOrder(event.order)
                setState {
                    copy(order = event.order,page = 1, shippingList = emptyList(), loadingState = Loading.LOADING)
                }
                getShipping(state.keyword.text, sort = state.sort, order = event.order)
            }
            is ShippingContract.Event.OnRemoveClick -> {
                removeShip(event.packingId)
            }
            is ShippingContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is ShippingContract.Event.OnSelectShip -> {
                setState {
                    copy(selectedShip = event.packingId)
                }
            }
            is ShippingContract.Event.OnShippingClick -> {
                setEffect {
                    ShippingContract.Effect.NavigateToShippingDetail(event.shippingRow)
                }
            }
            is ShippingContract.Event.OnShippingNumberChange -> {
                setState {
                    copy(shippingNumber = event.shippingNumber)
                }
            }
            is ShippingContract.Event.OnShowAddDialog -> {
                setState {
                    copy(showAddDialog = event.showAddDialog, shippingNumber = TextFieldValue(), selectedDriver = null, driverName = TextFieldValue())
                }
            }
            is ShippingContract.Event.OnShowFilterList -> {
                setState {
                    copy(showFilterList = event.showFilterList)
                }
            }
            is ShippingContract.Event.OnShowPopup -> {
                setState {
                    copy(showPopup = event.show)
                }
            }
            is ShippingContract.Event.OnSortChange -> {
                prefs.setShippingSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, shippingList = emptyList(), loadingState = Loading.LOADING, keyword = TextFieldValue())
                }
                getShipping(state.keyword.text, sort = event.sort, order = state.order)
            }

            ShippingContract.Event.OnReachEnd -> {
                if (10*state.page <= state.shippingList.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getShipping(page = state.page)
                }
            }

            ShippingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getShipping(state.keyword.text, sort = state.sort, order = state.order)
            }

            ShippingContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getShipping(state.keyword.text, state.page, sort = state.sort, order = state.order)
            }

            ShippingContract.Event.FetchData -> {
                setState {
                    copy(page = 1, shippingList = emptyList(), loadingState = Loading.LOADING)
                }
                getShipping(state.keyword.text, state.page, sort = state.sort, order = state.order)
            }

            is ShippingContract.Event.OnDriverNameChange -> {
                setState {
                    copy(driverName = event.name)
                }
            }
        }
    }

    private fun getShipping(
        keyword: String = "",
        page: Int = 1,
        sort: String = "CreatedOn",
        order: String = Order.Asc.value
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.getShipping(
                keyword, page,10,sort,order
            ).catch {
                setSuspendedState {
                    copy(error = it.message.toString(), loadingState = Loading.NONE)
                }
            }.collect {
                setSuspendedState {
                    copy(loadingState = Loading.NONE)
                }
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message, loadingState = Loading.NONE)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(shippingModel = it.data, shippingList = shippingList + (it.data?.rows?: emptyList()), loadingState = Loading.NONE)
                        }
                    }
                    else -> {}
                }
            }
        }
        getDrivers()
    }

    private fun getDrivers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDrivers()
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString())
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(driverList = it.data)
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun ship(driverId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(isShipping = true)
            }
            repository.ship(driverId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), isShipping = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isShipping = false, showAddDialog = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Ship added successfully", page = 1, shippingList = emptyList(), showAddDialog = false, loadingState = Loading.LOADING)
                            }
                            getShipping(state.keyword.text, sort = state.sort, order = state.order)
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun removeShip(shippingId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeShip(shippingId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), selectedShip = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(selectedShip = null)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message, selectedShip = null)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Ship removed successfully", shippingList = emptyList(), page = 1, selectedShip = null, loadingState = Loading.LOADING)
                            }
                            getShipping(state.keyword.text, sort = state.sort, order = state.order)
                        }
                        else -> {}
                    }
                }
        }
    }
}