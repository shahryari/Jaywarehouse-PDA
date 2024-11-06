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
    private val detail: ReceivingDetailRow
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
                setState {
                    copy(quantity = event.value)
                }
            }
            is CountingInceptionContract.Event.OnChangeQuantityInPacket -> {
                setState {
                    copy(quantityInPacket = event.value)
                }
            }
            is CountingInceptionContract.Event.OnShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.value)
                }
            }

            CountingInceptionContract.Event.OnAddClick -> {
                if (state.quantity.text.isEmpty() || state.quantityInPacket.text.isEmpty() || state.expireDate.text.isEmpty()) {
                    setState {
                        copy(error = "Please fill all fields")
                    }
                    return
                }
                val countItem = ReceivingDetailCountModel(
                    quantity = state.quantity.text.toInt() * state.quantityInPacket.text.toInt(),
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
                        quantityInPacket = TextFieldValue(),
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
                    copy(details = details.mapNotNull {
                        if (event.model.receivingWorkerTaskCountId == null && it.batchNumber == event.model.batchNumber && it.expireDate == event.model.expireDate && it.quantity == event.model.quantity) {
                            null
                        }
                        else if (it.receivingWorkerTaskCountId == event.model.receivingWorkerTaskCountId) {
                            it.copy(entityState = "Deleted")
                        } else it
                    })
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
                            copy(details = it.data?: emptyList())
                        }
                    }
                    else ->{}
                }
            }
        }
    }

    private fun countItems() {
        viewModelScope.launch {
            repository.countReceivingDetail(
                detail.receivingID,
                state.count,
                receivingTypeId = detail.receivingDetailID,
                counts = state.details
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