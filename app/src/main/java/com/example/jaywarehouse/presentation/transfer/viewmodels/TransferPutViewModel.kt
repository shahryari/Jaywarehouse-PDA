package com.example.jaywarehouse.presentation.transfer.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.putaway.model.ScanModel
import com.example.jaywarehouse.data.transfer.TransferRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.transfer.contracts.TransferContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TransferPutViewModel(
    private val repository: TransferRepository,
    private val prefs: Prefs
) : BaseViewModel<TransferContract.Event,TransferContract.State,TransferContract.Effect>(){
    init {
        setState {
            copy(sort = prefs.getTransferPutSort(),order = prefs.getTransferPutOrder())
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getTransfers(sort = prefs.getTransferPutSort(), order = prefs.getTransferPutOrder())
    }
    override fun setInitState(): TransferContract.State {
        return TransferContract.State(isPick = false)
    }

    override fun onEvent(event: TransferContract.Event) {
        when(event){
            TransferContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is TransferContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is TransferContract.Event.OnOrderChange -> {
                prefs.setTransferPutOrder(event.order)
                setState {
                    copy(order = event.order, page = 1, transferList = emptyList(), loadingState = Loading.LOADING)
                }
                getTransfers(state.keyword.text,sort = state.sort, order = event.order,page = state.page)
            }
            TransferContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, transferList = emptyList(), loadingState = Loading.SEARCHING)
                }
                getTransfers(state.keyword.text,sort = state.sort, order = state.order,page = state.page)
            }
            is TransferContract.Event.OnSortChange -> {
                prefs.setTransferPutSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, transferList = emptyList(), loadingState = Loading.LOADING)
                }
                getTransfers(state.keyword.text, sort = event.sort, order = state.order, page = state.page)
            }
            is TransferContract.Event.ShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }

            is TransferContract.Event.OnBarcodeChange -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            is TransferContract.Event.OnBoxNumberChange -> {
                setState {
                    copy(boxNumber = event.boxNumber)
                }
            }
            is TransferContract.Event.OnLocationCodeChange ->{
                setState {
                    copy(locationCode = event.locationCode)
                }
            }
            TransferContract.Event.OnReachToEnd -> {
                if(10*state.page <= state.transferList.size) {
                    setState {
                        copy(page = page + 1, loadingState = Loading.LOADING)
                    }
                    getTransfers(
                        state.keyword.text,
                        sort = state.sort,
                        order = state.order,
                        page = state.page
                    )
                }
            }
            is TransferContract.Event.OnShowTransferBox -> {
                setState {
                    copy(showTransferBox = event.show, barcode = TextFieldValue(), locationCode = TextFieldValue(), boxNumber = TextFieldValue(), showBoxButton = false)
                }
            }
            is TransferContract.Event.OnShowTransferItem -> {
                setState {
                    copy(showTransferItem = event.show, barcode = TextFieldValue(), locationCode = TextFieldValue(), boxNumber = TextFieldValue(), showBoxButton = false)
                }
            }
            TransferContract.Event.OnTransferBox -> {
                putTransfer("", state.boxNumber.text, state.locationCode.text)
            }
            TransferContract.Event.OnTransferItem -> {
                putTransfer(state.barcode.text, state.boxNumber.text, state.locationCode.text)
            }

            TransferContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is TransferContract.Event.ShowTransferButton -> {
                setState {
                    copy(showBoxButton = event.show)
                }
            }

            TransferContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, transferList = emptyList(), loadingState = Loading.REFRESHING)
                }
                getTransfers(state.keyword.text,sort = state.sort, order = state.order,page = state.page)
            }

            TransferContract.Event.LoudSettings -> {

            }
        }
    }

    private fun putTransfer(barcode: String, boxNumber: String, locationCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            setSuspendedState {
                copy(isTransferring = true)
            }
            repository.putTransfer(locationCode = locationCode,barcode = barcode, boxNumber = boxNumber)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), isTransferring = false)
                    }
                }
                .collect {
                    when(it){
                        is BaseResult.Error -> {
                            val data = if (it.message.isNotEmpty()) {
                                try {
                                    Gson().fromJson(it.message, ScanModel::class.java).message
                                }catch (e:Exception){
                                    it.message
                                }
                            } else it.data?.messages?.firstOrNull()
                            setSuspendedState {
                                copy(error = data?:"", isTransferring = false)
                            }
                        }
                        is BaseResult.Success -> {
                            setState {
                                copy(page = 1, transferList = emptyList(), isTransferring = false, showTransferItem = false, showTransferBox = false, loadingState = Loading.LOADING)
                            }
                            getTransfers(keyword = state.keyword.text, sort = state.sort, order = state.order, page = state.page)
                        }
                        else -> {
                            setState {
                                copy(isTransferring = false)
                            }
                        }
                    }
                }
        }
    }

    private fun getTransfers(
        keyword: String = "",
        sort: String = "CreatedOn",
        order: String = Order.Asc.value,
        page: Int = 1
    ){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransfers(keyword,page,10,sort,order)
                .catch {
                    setSuspendedState {
                        copy(error = it.message.toString(), loadingState = Loading.NONE)
                    }
                }
                .collect {
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
                                copy(transferModel = it.data, transferList = transferList+(it.data?.rows ?: emptyList()))
                            }
                        }
                        else -> {
                        }
                    }
                }
        }
    }
}