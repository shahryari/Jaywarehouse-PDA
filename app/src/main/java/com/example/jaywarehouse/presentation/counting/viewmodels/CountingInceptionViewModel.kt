package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.presentation.counting.contracts.CountingInceptionContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountingInceptionViewModel(
    repository: ReceivingRepository,
    prefs: Prefs,
    detail: ReceivingDetailRow
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
        }
    }
}