package com.example.jaywarehouse.presentation.transfer.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.transfer.TransferRepository
import com.example.jaywarehouse.data.transfer.models.TransferRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.transfer.contracts.TransferContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TransferViewModel(
    private val repository: TransferRepository,
    private val prefs: Prefs
) : BaseViewModel<TransferContract.Event,TransferContract.State,TransferContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getTransferSort() && it.order == Order.getFromValue(prefs.getTransferOrder())
        }
        if (sort!=null) setState {
            copy(sort = sort)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    override fun setInitState(): TransferContract.State {
        return TransferContract.State()
    }

    override fun onEvent(event: TransferContract.Event) {
        when(event){
            is TransferContract.Event.OnChangeKeyword ->{
                setState {
                    copy(keyword = event.keyword)
                }
            }

            TransferContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }
            is TransferContract.Event.OnChangeSort -> {
                prefs.setTransferSort(event.sort.sort)
                prefs.setTransferOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, transferList = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getTransferList()
            }
            is TransferContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.showSortList)
                }
            }

            TransferContract.Event.ReloadScreen -> {

                setState {
                    copy(page = 1, transferList = emptyList(), loadingState = Loading.LOADING, keyword = TextFieldValue())
                }

                getTransferList()
            }

            TransferContract.Event.OnReachedEnd -> {
                if (10*state.page<=state.transferList.size) {
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getTransferList()
                }
            }

            TransferContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, transferList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getTransferList()
            }

            TransferContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, transferList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getTransferList()
            }

            TransferContract.Event.OnBackPressed -> {
                setEffect {
                    TransferContract.Effect.NavBack
                }
            }

            TransferContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            is TransferContract.Event.OnChangeDestination -> {
                setState {
                    copy(destination = event.destination)
                }
                getWarehouseLocations(event.destination.text)
            }
            is TransferContract.Event.OnChangeExpirationDate -> {
                setState {
                    copy(expirationDate = event.expirationDate)
                }
            }
            is TransferContract.Event.OnChangeProductStatus -> {
                setState {
                    copy(productStatus = event.productStatus)
                }
            }
            is TransferContract.Event.OnChangeQuantity -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is TransferContract.Event.OnSelectTransfer -> {
                setState {
                    copy(selectedTransfer = event.transferRow)
                }
                if (event.transferRow!=null){
                    getProductStatuses()
                }
            }
            is TransferContract.Event.OnTransfer -> {
                transfer(event.row)
            }

            is TransferContract.Event.OnShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.show)
                }
            }

            is TransferContract.Event.OnSelectProductStatus -> {
                setState {
                    copy(selectedProductStatus = event.productStatus)
                }
            }
            is TransferContract.Event.OnSelectWarehouseLocation -> {
                setState {
                    copy(selectedLocation = event.location)
                }
            }
        }
    }



    private fun getWarehouseLocations(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWarehouseLocations(
                location
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"")
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
                            copy(
                                locationList = it.data?.rows?: emptyList()
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun getProductStatuses(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductStatuses()
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"")
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
                                copy(productStatusList = it.data?.rows?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }
    private fun getTransferList(
    ) {
        viewModelScope.launch {
            repository.getTransfers(
                keyword = state.keyword.text,state.page,state.sort.sort,state.sort.order.value
            )
                .catch {
                    setSuspendedState {
                        copy(error = it.message?:"", loadingState = Loading.NONE)
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                    when(it){
                        is BaseResult.Error ->  {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(transferList = transferList + (it.data?.rows?: emptyList()), loadingState = Loading.NONE)
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
        }
    }


    private fun transfer(transfer: TransferRow) {
        if (state.selectedProductStatus!=null && state.selectedLocation!=null){
            setState {
                copy(isSaving = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.transferLocation(
                    state.selectedProductStatus!!.quiddityTypeId,
                    state.selectedLocation!!.locationId,
                    state.selectedLocation!!.locationCode,
                    transfer.locationInventoryID,
                    state.expirationDate.text,
                    transfer.warehouseID
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isSaving = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(
                            isSaving = false
                        )
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            setSuspendedState {
                                copy(
                                    selectedTransfer = null,
                                    transferList = emptyList(),
                                    page = 1,
                                    loadingState = Loading.LOADING
                                )
                            }
                            getTransferList()
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        } else {
            setState {
                copy(error = "Please select location and product status.")
            }
        }
    }

}