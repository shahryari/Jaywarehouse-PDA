package com.example.jaywarehouse.presentation.manual_putaway.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.manual_putaway.ManualPutawayRepository
import com.example.jaywarehouse.data.manual_putaway.repository.ManualPutawayRow
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.manual_putaway.contracts.ManualPutawayDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ManualPutawayDetailViewModel(
    private val repository: ManualPutawayRepository,
    private val prefs: Prefs,
    private val put: ManualPutawayRow
) : BaseViewModel<ManualPutawayDetailContract.Event,ManualPutawayDetailContract.State,ManualPutawayDetailContract.Effect>(){

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getManualPutawayDetailSort() && it.order == Order.getFromValue(prefs.getManualPutawayDetailOrder())
        }
        if (sort != null) {
            setState {
                copy(selectedSort = sort)
            }
        }
        setState {
            copy(putaway = put)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getManualPutawayDetails()
    }


    override fun setInitState(): ManualPutawayDetailContract.State {
        return ManualPutawayDetailContract.State()
    }

    override fun onEvent(event: ManualPutawayDetailContract.Event) {
        when(event) {
            ManualPutawayDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }
            ManualPutawayDetailContract.Event.OnAddClick -> {
                if (state.quantity.text.isNotEmpty() && state.quantityInPacket.text.isNotEmpty() && state.locationCode.text.isNotEmpty()) {
                    scanManualPutaway()
                }
            }
            ManualPutawayDetailContract.Event.OnCloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is ManualPutawayDetailContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is ManualPutawayDetailContract.Event.OnLocationCodeChange -> {
                setState {
                    copy(locationCode = event.locationCode)
                }
            }
            ManualPutawayDetailContract.Event.OnNavBack -> {
                setEffect {
                    ManualPutawayDetailContract.Effect.NavBack
                }
            }
            is ManualPutawayDetailContract.Event.OnQuantityChange -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is ManualPutawayDetailContract.Event.OnQuantityInPacketChange ->  {
                setState {
                    copy(quantityInPacket = event.quantity)
                }
            }
            ManualPutawayDetailContract.Event.OnReachEnd -> {
                if (10*state.page <= state.details.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getManualPutawayDetails()
                }
            }
            ManualPutawayDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, loadingState = Loading.REFRESHING, details = emptyList())
                }
            }
            is ManualPutawayDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is ManualPutawayDetailContract.Event.OnSortChange -> {
                setState {
                    copy(selectedSort = event.sort,page = 1, details = emptyList(), loadingState = Loading.LOADING)
                }
                getManualPutawayDetails()

            }
            ManualPutawayDetailContract.Event.OnSubmit ->  {
                finishManualPutaway()
            }

            is ManualPutawayDetailContract.Event.OnRemove -> {
                removeManualPutaway(event.detail.productLocationActivityId)
            }
            is ManualPutawayDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(selectedDetail = event.detail)
                }
            }

            ManualPutawayDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, details = emptyList(), loadingState = Loading.SEARCHING)
                }
                getManualPutawayDetails()
            }

            is ManualPutawayDetailContract.Event.OnShowConfirmFinish -> {
                setState {
                    copy(showConfirmFinish = event.show)
                }
            }
        }
    }

    private fun scanManualPutaway() {
        setState {
            copy(isScanning = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.scanManualPutaway(
                locationCode = state.locationCode.text,
                quantity = state.quantity.text.toInt() * state.quantityInPacket.text.toInt(),
                warehouseId = put.warehouseID.toString(),
                receiptDetailId = put.receiptDetailID.toString(),
                receiptId = put.receiptID.toString(),
                productInventoryId = put.productInventoryID.toString(),
                productId = put.productID.toString()
            ).catch {
                setState {
                    copy(error = it.message?:"", isScanning = false)
                }
            }.collect {
                setSuspendedState {
                    copy(isScanning = false)
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
                                quantity = TextFieldValue(),
                                quantityInPacket = TextFieldValue(),
                                locationCode = TextFieldValue(),
                                loadingState = Loading.LOADING,
                                details = emptyList(),
                                page = 1,
                                toast = it.data?.messages?.firstOrNull() ?: "Added Successfully"
                            )
                        }
                        getManualPutawayDetails()
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun removeManualPutaway(productLocationActivityId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeManualPutaway(
                productLocationActivityId
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"", selectedDetail = null)
                }
            }.collect {
                setSuspendedState {
                    copy(selectedDetail = null)
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
                                toast = it.data?.messages?.firstOrNull() ?: "Removed Successfully",
                                loadingState = Loading.LOADING,
                                page = 1,
                                details = emptyList()
                            )
                        }
                        getManualPutawayDetails()
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun getManualPutawayDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getManualPutawayDetail(
                state.keyword.text,
                put.receiptDetailID.toString(),
                state.page,
                state.selectedSort.sort,
                state.selectedSort.order.value
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"", loadingState = Loading.NONE)
                }
            }.collect {
                setSuspendedState {
                    copy(loadingState = Loading.NONE)
                }
                when(it) {
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }

                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                count = it.data?.total ?: 0,
                                details = details + (it.data?.rows?: emptyList())
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun finishManualPutaway() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.finishManualPutaway(put.receiptDetailID.toString())
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
                            setEffect {
                                ManualPutawayDetailContract.Effect.NavBack
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }
}