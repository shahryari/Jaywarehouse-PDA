package com.linari.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.receiving.model.ReceivingDetailCountModel
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.receiving.repository.ReceivingRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.counting.contracts.CountingContract
import com.linari.presentation.counting.contracts.CountingInceptionContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingInceptionViewModel(
    private val repository: ReceivingRepository,
    prefs: Prefs,
    private val detail: ReceivingDetailRow,
    private val isCrossDock: Boolean = false,
    private val receivingId: Int,
) : BaseViewModel<CountingInceptionContract.Event,CountingInceptionContract.State,CountingInceptionContract.Effect>(){
    init {
        setState {
            copy(countingDetailRow = detail,quantityInPacket = TextFieldValue(detail.pcb?.toInt()?.toString()?:""))
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
                done()
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
                insert()
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
                delete(event.model)
            }

            is CountingInceptionContract.Event.OnSelectedItem -> {
                setState {
                    copy(selectedItem = event.item)
                }
            }

            is CountingInceptionContract.Event.OnShowConfirmDialog -> {
                setState {
                    copy(showConfirm = event.show)
                }
            }

            CountingInceptionContract.Event.OnReachEnd -> {
                if (ROW_COUNT*state.page <= state.details.size){
                    setState {
                        copy(page = page+1)
                    }
                    getItems()
                }
            }

            CountingInceptionContract.Event.OnAddWeight -> {
                insertWeight()
            }

            is CountingInceptionContract.Event.OnChangeBoxQuantity -> {
                setState {
                    copy(boxQuantity = event.value)
                }
            }

            CountingInceptionContract.Event.OnRefresh -> {
                setState {
                    copy(details = emptyList(), page = 1)
                }
                getItems(Loading.REFRESHING)
            }

            is CountingInceptionContract.Event.OnSelectedDateChange -> {
                setState {
                    copy(selectedDate = event.date)
                }
            }
        }
    }


    private fun getItems(loading: Loading = Loading.LOADING) {
        if (state.loadingState == Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getReceivingDetailCountModel(
                    detail.receivingWorkerTaskID,
                    state.page,
                    isCrossDock
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
                            val detailList = state.details + (it.data?.rows?: emptyList())
                            setSuspendedState {
                                copy(
                                    details = detailList,
                                    countingDetailRow = it.data?.receivingDetailRow,
                                    quantityInPacket = TextFieldValue(it.data?.pcb?.pcb?.toString()?:it.data?.pcb?.defaultPcb?.toString()?:""),
                                    pcbEnabled =it.data?.pcb?.pcb == null,
                                    locationBase = it.data?.pcb?.locationBase == true,
                                    expEnabled = it.data?.pcb?.expired == true,
                                    batchNumberEnabled = it.data?.pcb?.hasBatchNumber == true
                                )
                            }
                        }
                        else ->{}
                    }
                }
            }
        }
    }

    fun insertWeight() {
        if (state.quantity.text.isEmpty() || state.quantityInPacket.text.isEmpty() || state.boxQuantity.text.isEmpty()) {
            setState {
                copy(error = "Please fill all fields")
            }
            return
        }
        val quantity = state.quantity.text.toDoubleOrNull()?:0.0
//        if (quantity<=0){
//            setState {
//                copy(error = "Quantity must be greater than 0")
//            }
//            return
//        }
        val pcb = state.quantityInPacket.text.toDoubleOrNull()?:0.0
        if (state.details.isNotEmpty() && state.details.sumOf { it.countQuantity } == 0.0){
            setState {
                copy(error = "ِYou already count 0 for this product.")
            }
            return
        }
        if (detail.pcb!=null && pcb<1.0){
            setState {
                copy(error = "Pcb must be greater then 0")
            }
            return
        }
        val pack = state.boxQuantity.text.toDoubleOrNull()
        if ((pack?:0.0)<1.0 && pcb>1.0 && quantity > 0){
            setState {
                copy(error = "Box Quantity must be greater then 0")
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
        if (state.batchNumber.text.trim().isNotEmpty() && state.expireDate.text.trim().isNotEmpty()){
            if (state.details.find { it.batchNumber == state.batchNumber.text.trim() && it.expireDate == state.expireDate.text.trim() } != null){
                setState {
                    copy(error = "This combination of batch number and expire date currently exists.")
                }
                return
            }
        } else {
            if (state.details.find { it.countQuantity == quantity.toDouble() && it.expireDate == state.expireDate.text.trim() } != null){
                setState {
                    copy(error = "Item with same Quantity already exists")
                }
                return
            }
        }
        if (!state.isAdding) {
            if (!state.isAdding){
                setState {
                    copy(isAdding = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    repository.receivingWorkerTaskCountInsert(
                        detail.receivingWorkerTaskID,
                        quantity,
                        pcb,
                        pack?.toInt(),
                        state.selectedDate.trim(),
                        state.batchNumber.text.trim(),
                        isCrossDock
                    ).catch {
                        setSuspendedState {
                            copy(error = it.message?:"", isAdding = false)
                        }
                    }.collect {
                        setSuspendedState {
                            copy(isAdding = false)
                        }
                        when(it){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = it.message)
                                }
                            }
                            is BaseResult.Success -> {
                                if (it.data?.isSucceed == true){
                                    setSuspendedState {
                                        copy(
                                            details = emptyList(),
                                            page = 1,
                                            toast = "Item add Successfully"
                                        )
                                    }
                                    getItems()
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
    }
    fun insert() {
        val pcb = state.quantityInPacket.text.toDoubleOrNull()?:0.0
        if (state.quantity.text.isEmpty() || (state.quantityInPacket.text.isEmpty()) || (pcb> 1.0 && state.boxQuantity.text.isEmpty())) {
            setState {
                copy(error = "Please fill all fields")
            }
            return
        }
        val quantity = state.quantity.text.toDoubleOrNull()?:0.0
        if (quantity<0){
            setState {
                copy(error = "Quantity must be equal to 0 or greater than 0")
            }
            return
        }
        if (state.details.isNotEmpty() && state.details.sumOf { it.countQuantity } == 0.0){
            setState {
                copy(error = "ِYou already count 0 for this product.")
            }
            return
        }
        if (pcb<1.0){
            setState {
                copy(error = "Pcb must be greater then 0")
            }
            return
        }
        if ( quantity%pcb != 0.0) {
            setState {
                copy(error = "Quantity has wrong value")
            }
            return
        }
        val pack = state.boxQuantity.text.toDoubleOrNull()
        if ((pack?:0.0)<1 && pcb>1.0 && quantity > 0){
            setState {
                copy(error = "Box Quantity must be greater then 0")
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
        if (state.batchNumber.text.trim().isNotEmpty() && state.expireDate.text.trim().isNotEmpty()){
            if (state.details.find { it.batchNumber == state.batchNumber.text.trim() && it.expireDate == state.expireDate.text.trim() } != null){
                setState {
                    copy(error = "This combination of batch number and expire date currently exists.")
                }
                return
            }
        } else {
            if (state.details.find { it.countQuantity == quantity.toDouble() && it.expireDate == state.expireDate.text.trim() } != null){
                setState {
                    copy(error = "Item with same Quantity already exists")
                }
                return
            }
        }
        if (!state.isAdding){
            setState {
                copy(isAdding = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.receivingWorkerTaskCountInsert(
                    detail.receivingWorkerTaskID,
                    quantity,
                    pcb,
                    pack?.toInt(),
                    state.selectedDate.trim(),
                    state.batchNumber.text.trim(),
                    isCrossDock
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isAdding = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isAdding = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                setSuspendedState {
                                    copy(
                                        details = emptyList(),
                                        page = 1,
                                        quantity = TextFieldValue(),
                                        batchNumber = TextFieldValue(),
                                        expireDate = TextFieldValue(),
                                        toast = "Item add Successfully"
                                    )
                                }
                                getItems()
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

    fun delete(item: ReceivingDetailCountModel) {
        if (!state.isDeleting) {
            setState {
                copy(isDeleting = true)
            }
            viewModelScope.launch(Dispatchers.IO){
                repository.receivingWorkerTaskCountDelete(
                    item.receivingWorkerTaskCountID.toString(),
                    isCrossDock
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isDeleting = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(
                            isDeleting = false,
                            selectedItem = null
                        )
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                setSuspendedState {
                                    copy(
                                        details = emptyList(),
                                        page = 1,
                                        toast = it.data.messages.firstOrNull()?:"Item deleted successfully."
                                    )
                                }
                                getItems()
                            } else {
                                setSuspendedState {
                                    copy(error = it.data?.messages?.firstOrNull()?:"Failed")
                                }
                            }
                        }
                        BaseResult.UnAuthorized -> {

                        }
                    }
                }
            }
        }
    }

    fun done() {
        if (!state.isCompleting) {
            setState {
                copy(isCompleting = true)
            }
            viewModelScope.launch(Dispatchers.IO){
                repository.receivingWorkerTaskDone(
                    detail.receivingWorkerTaskID,
                    isCrossDock
                ).catch {
                    setSuspendedState {
                        copy(error = it.message?:"", isCompleting = false)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isCompleting = false)
                    }
                    when(it){
                        is BaseResult.Error -> {
                            setSuspendedState {
                                copy(error = it.message)
                            }
                        }
                        is BaseResult.Success -> {
                            if (it.data?.isSucceed == true){
                                setSuspendedState {
                                    copy(toast = it.data.messages.firstOrNull()?:"Finished successfully")
                                }
                                setEffect {
                                    CountingInceptionContract.Effect.NavBack
                                }
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
}