package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailCountModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingInceptionViewModel(
   private val repository: ReceivingRepository,
    prefs: Prefs,
    private val detail: ReceivingDetailRow,
    private val receivingId: Int,
) : BaseViewModel<CountingInceptionContract.Event,CountingInceptionContract.State,CountingInceptionContract.Effect>(){
    init {
        setState {
            copy(countingDetailRow = detail)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(hideKeyboard = it)
                }
            }
        }
        getItems()
    }
    override fun setInitState(): CountingInceptionContract.State {
        return CountingInceptionContract.State()
    }

    override fun onEvent(event: CountingInceptionContract.Event) {
        when(event){
            CountingInceptionContract.Event.OnBack -> {
                setEffect {
                    CountingInceptionContract.Effect.NavBack
                }
            }
            CountingInceptionContract.Event.OnSubmit -> {
                countItems()
            }
            is CountingInceptionContract.Event.OnChangeBatchNumber -> {
                setState {
                    copy(batchNumber = event.value)
                }
            }
            is CountingInceptionContract.Event.OnChangeExpireDate -> {
                setState {
                    copy(expireDate = event.value)
                }
            }
            is CountingInceptionContract.Event.OnChangeQuantity -> {
                val quantity = event.value.text.toIntOrNull() ?: 0
                val inPack = state.quantityInPacket.text.toIntOrNull() ?: 1
                setState {
                    copy(quantity = event.value, count = quantity*inPack)
                }
            }
            is CountingInceptionContract.Event.OnChangeQuantityInPacket -> {

                val quantity = state.quantity.text.toIntOrNull() ?: 0
                val inPack = event.value.text.toIntOrNull() ?: 1
                setState {
                    copy(quantityInPacket = event.value, count = quantity*inPack)
                }
            }
            is CountingInceptionContract.Event.OnShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.value)
                }
            }

            CountingInceptionContract.Event.OnAddClick -> {
                if (state.quantity.text.isEmpty() || state.quantityInPacket.text.isEmpty()) {
                    setState {
                        copy(error = "Please fill all fields")
                    }
                    return
                }
                val quantity = (state.quantity.text.toIntOrNull()?:0) * (state.quantityInPacket.text.toIntOrNull()?:0)
                if (quantity<0){
                    setState {
                        copy(error = "Quantity must be equal to 0 or greater than 0")
                    }
                    return
                }
                if (detail.batchNumber!=null && state.batchNumber.text.isEmpty()){
                    setState {
                        copy(error = "Please fill batch number")
                    }
                    return
                }
                if (detail.expireDate!=null && state.expireDate.text.isEmpty()){
                    setState {
                        copy(error = "Please fill expire date")
                    }
                    return
                }
                if (state.batchNumber.text.trim().isNotEmpty()){
                    if (state.details.find { it.batchNumber == state.batchNumber.text.trim() } != null){
                        setState {
                            copy(error = "Batch number already exists")
                        }
                        return
                    }
                } else {
                    if (state.details.find { it.quantity == quantity && it.expireDate == state.expireDate.text.trim() } != null){
                        setState {
                            copy(error = "Item with same Quantity already exists")
                        }
                        return
                    }
                }
                val countItem = ReceivingDetailCountModel(
                    quantity = quantity,
                    batchNumber = state.batchNumber.text,
                    expireDate = state.expireDate.text,
                    entityState = "Added",
                    receivingWorkerTaskCountId = null,
                    receivingWorkerTaskId = detail.receivingWorkerTaskID
                )
                setState {
                    copy(
                        details = details + countItem,
                        quantity = TextFieldValue(),
                        quantityInPacket = TextFieldValue("1"),
                        batchNumber = TextFieldValue(),
                        expireDate = TextFieldValue()
                    )
                }
            }

            CountingInceptionContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            CountingInceptionContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is CountingInceptionContract.Event.OnDeleteCount -> {
                setState {
                    copy(details = details.mapIndexedNotNull { index, it ->
                        if (state.selectedIndex == index){

                            if (event.model.receivingWorkerTaskCountId == null) {
                                null
                            }
                            else if (it.receivingWorkerTaskCountId == event.model.receivingWorkerTaskCountId) {
                                it.copy(entityState = "Deleted")
                            } else it
                        } else {
                            it
                        }
                    }, selectedItem = null, selectedIndex = null)
                }
            }

            is CountingInceptionContract.Event.OnSelectedItem -> {
                setState {
                    copy(selectedItem = event.item, selectedIndex = event.index)
                }
            }

            is CountingInceptionContract.Event.OnShowConfirmDialog -> {
                setState {
                    copy(showConfirm = event.show)
                }
            }
        }
    }


    private fun getItems() {
        setState {
            copy(loadingState = Loading.LOADING)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingDetailCountModel(
                detail.receivingWorkerTaskID
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"", loadingState = Loading.NONE)
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
                            copy(details = it.data?.rows?: emptyList())
                        }
                    }
                    else ->{}
                }
            }
        }
    }

    private fun countItems() {
        if (state.details.isEmpty()){
            setState {
                copy(error = "Please add at least one item")
            }
            return
        }
        viewModelScope.launch {
            repository.countReceivingDetail(
                receivingId,
                state.details.sumOf { it.quantity },
                receivingTypeId = detail.receivingTypeID,
                counts = state.details
            ).catch {
                setSuspendedState {
                    copy(error = it.message?:"", showConfirm = false)
                }
            }.collect {
                setSuspendedState {
                    copy(showConfirm = false)
                }
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(error = it.message)
                        }
                    }
                    is BaseResult.Success -> {
                        setState {
                            copy(toast = it.data?.messages?.firstOrNull()?: "Finished Successfully")
                        }
                        setEffect {
                            CountingInceptionContract.Effect.NavBack
                        }
                    }
                    else ->{}
                }
            }
        }
    }
}