package com.example.jaywarehouse.presentation.packing.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.packing.PackingRepository
import com.example.jaywarehouse.data.packing.model.PackingCustomerModel
import com.example.jaywarehouse.data.packing.model.PackingCustomerModelItem
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.packing.contracts.PackingContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PackingViewModel(
    private val repository: PackingRepository,
    private val prefs: Prefs
) : BaseViewModel<PackingContract.Event, PackingContract.State, PackingContract.Effect>() {
    init {
        setState {
            copy(sort = prefs.getPackingSort(), order = prefs.getPackingOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): PackingContract.State {
        return PackingContract.State( )
    }

    override fun onEvent(event: PackingContract.Event) {
        when(event){
            PackingContract.Event.OnClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is PackingContract.Event.OnOrderChange -> {
                prefs.setPackingOrder(event.order)
                setState {
                    copy(order = event.order, packings = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getPackingList(
                    state.keyword.text,
                    page = state.page,
                    sort = state.sort,
                    order = event.order
                )
            }
            is PackingContract.Event.OnPackingClick -> {
                setEffect {
                    PackingContract.Effect.NavigateToPackingDetail(event.packingRow)
                }
            }
            is PackingContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is PackingContract.Event.OnShowFilterList -> {
                setState {
                    copy(showFilterList = event.showFilterList)
                }
            }
            is PackingContract.Event.OnSortChange -> {
                prefs.setPackingSort(event.sort)
                setState {
                    copy(sort = event.sort, packings = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getPackingList(state.keyword.text, page = 1, sort = event.sort, order = state.order)
            }

            PackingContract.Event.OnAddClick -> {
                addPacking(state.packingNumber.text,state.selectedCustomer?.customerID?:-1)
            }
            is PackingContract.Event.OnCustomerChange -> {
                setState {
                    copy(selectedCustomer = event.customer, showPopup = false)
                }
            }
            is PackingContract.Event.OnPackingNumberChange -> {
                setState {
                    copy(packingNumber = event.packingNumber)
                }
            }
            is PackingContract.Event.OnRemoveClick -> {
                removePacking(event.packingId)
            }
            is PackingContract.Event.OnShowAddDialog -> {
                setState {
                    copy(showAddDialog = event.showAddDialog, packingNumber = TextFieldValue(), selectedCustomer = null, customerName = TextFieldValue())
                }
            }
            PackingContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is PackingContract.Event.OnSelectPack -> {
                setState {
                    copy(selectedPack = event.packingId)
                }
            }

            is PackingContract.Event.OnShowPopup -> {
                setState {
                    copy(showPopup = event.show)
                }
            }

            PackingContract.Event.OnReachedEnd -> {
                if (10*state.page <= state.packings.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getPackingList(
                        state.keyword.text,
                        page = state.page,
                        sort = state.sort,
                        order = state.order
                    )
                }
            }

            PackingContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, loadingState = Loading.SEARCHING, packings = emptyList())
                }
                getPackingList(
                    state.keyword.text,
                    page = state.page,
                    sort = state.sort,
                    order = state.order
                )
            }

            PackingContract.Event.OnRefresh -> {
                setState {
                    copy(packings = emptyList(),page = 1, loadingState = Loading.REFRESHING)
                }
                getPackingList(state.keyword.text,state.page,state.sort,state.order)
            }

            PackingContract.Event.FetchData -> {
                setState {
                    copy(packings = emptyList(), page = 1, loadingState = Loading.LOADING, keyword = TextFieldValue())
                }
                getPackingList(state.keyword.text,state.page,state.sort,state.order)
            }

            is PackingContract.Event.OnCustomerNameChange -> {
                setState {
                    copy(customerName = event.customerName)
                }
            }
        }
    }

    private fun getPackingList(
        keyword: String = "",
        page: Int = 1,
        sort: String = "CreatedOn",
        order: String = Order.Asc.value
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPacking(
                keyword,page,10,sort,order
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
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(packingModel = it.data, packings = packings+(it.data?.rows?: emptyList()))
                        }
                    }

                    BaseResult.UnAuthorized -> {}
                }
            }
        }
        getCustomers()
    }

    private fun addPacking(packingNumber: String,customerId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(isPacking = true)
            }
            repository.pack(packingNumber,customerId)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), isPacking = false)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(showAddDialog = false, isPacking = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message
                            setSuspendedState {
                                copy(error = data?:"")
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Item Successfully Packed", page = 1, packings = emptyList(), loadingState = Loading.LOADING)
                            }
                            getPackingList(
                                state.keyword.text,
                                sort = state.sort,
                                order = state.order
                            )
                        }
                        is BaseResult.UnAuthorized -> {
                        }
                    }
                }
        }
    }

    private fun getCustomers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPackingCustomers("",1,1000,"CreatedOn",Order.Asc.value)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString())
                    }
                }.collect {
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(customerList = it.data, selectedCustomer = it.data?.firstOrNull())
                            }
                        }
                        else ->{}
                    }
                }
        }
    }

    private fun removePacking(packingId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.packRemove(packingId)
                .catch {
                    setSuspendedState {
                        copy(error = "", selectedPack = null)
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            val data = if(it.message.isNotEmpty()){
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.message
                            setSuspendedState {
                                copy(error = data?:"", selectedPack = null)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(toast = "Item Successfully Removed", page = 1, packings = emptyList(), loadingState = Loading.LOADING, selectedPack = null)
                            }
                            getPackingList(
                                state.keyword.text,
                                sort = state.sort,
                                order = state.order
                            )
                        }
                        else -> {
                            setSuspendedState {
                                copy(selectedPack = null)
                            }
                        }
                    }
                }
        }
    }
}