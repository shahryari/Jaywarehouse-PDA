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
import org.koin.core.component.getScopeName
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

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
                if (state.quantity.text.isNotEmpty() && state.locationCode.text.isNotEmpty()) {
                    scanManualPutaway()
                }
            }
            ManualPutawayDetailContract.Event.OnCloseError -> {
                setState {
                    copy(error = "")
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
                getManualPutawayDetails()
            }
            is ManualPutawayDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is ManualPutawayDetailContract.Event.OnSortChange -> {
                prefs.setManualPutawayDetailSort(event.sort.sort)
                prefs.setManualPutawayDetailOrder(event.sort.order.value)
                setState {
                    copy(selectedSort = event.sort,page = 1, details = emptyList(), loadingState = Loading.LOADING)
                }
                getManualPutawayDetails()

            }
            ManualPutawayDetailContract.Event.OnSubmit ->  {
                finishManualPutaway()
            }

            is ManualPutawayDetailContract.Event.OnRemove -> {
                removeManualPutaway(event.detail.putawayDetailID.toString())
            }
            is ManualPutawayDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(selectedDetail = event.detail)
                }
            }

            is ManualPutawayDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, details = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
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
        val quantity = state.details.sumOf { it.quantity } + state.quantity.text.toDouble()
        if (quantity.toBigDecimal().setScale(4, RoundingMode.UNNECESSARY).toDouble() > put.total){
            setState {
                copy(error = "Total scanned quantity is more then required quantity")
            }
            return
        }
        if (!state.isScanning){
            setState {
                copy(isScanning = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.scanManualPutaway(
                    locationCode = state.locationCode.text,
                    quantity = state.quantity.text.toDouble(),
                    warehouseId = put.warehouseID.toString(),
                    putawayId = put.putawayID
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
                            if (it.data?.isSucceed == true) {
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
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    private fun removeManualPutaway(putawayDetailID: String) {
        if (!state.isDeleting) {
            setState {
                copy(isDeleting = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.removeManualPutaway(
                    putawayDetailID
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isDeleting = false, selectedDetail = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isDeleting = false,selectedDetail = null)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true) {
                                setSuspendedState {
                                    copy(
                                        toast = it.data?.messages?.firstOrNull() ?: "Removed Successfully",
                                        loadingState = Loading.LOADING,
                                        page = 1,
                                        details = emptyList()
                                    )
                                }
                                getManualPutawayDetails()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    private fun getManualPutawayDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getManualPutawayDetail(
                state.keyword,
                put.putawayID,
                state.page,
                "CreatedOn",
                Order.Desc.value
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
                                details = details + (it.data?.rows?: emptyList()),
                                putaway = it.data?.task
                            )
                        }
                    }
                    BaseResult.UnAuthorized -> {}
                }
            }
        }
    }

    private fun finishManualPutaway() {
        val quantity = state.details.sumOf { it.quantity }
        if ( quantity.toBigDecimal().setScale(4, RoundingMode.UNNECESSARY).toDouble() > put.total){
            setState {
                copy(error = "You have scanned more than the quantity")
            }
            return
        }
        if (quantity.toBigDecimal().setScale(4, RoundingMode.UNNECESSARY).toDouble()< put.total) {
            setState {
                copy(error = "Your total scanned is less then required quantity.")
            }
            return
        }
        if (!state.isFinishing){
            setState {
                copy(isFinishing = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.finishManualPutaway(put.receiptDetailID.toString(),put.putawayID.toString())
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isFinishing = false)
                        }
                    }
                    .collect {
                        setSuspendedState {
                            copy(isFinishing = false)
                        }
                        when(it){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true) {
                                    setSuspendedState {
                                        copy(showConfirmFinish = false)
                                    }
                                    setEffect {
                                        ManualPutawayDetailContract.Effect.NavBack
                                    }
                                } else {
                                    setSuspendedState {
                                        copy(error = it.data?.messages?.firstOrNull()?:"")
                                    }
                                }
                            }
                            BaseResult.UnAuthorized -> {}
                        }
                    }
            }
        }
    }
}