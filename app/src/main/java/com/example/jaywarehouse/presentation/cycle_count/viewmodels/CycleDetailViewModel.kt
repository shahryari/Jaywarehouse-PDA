package com.example.jaywarehouse.presentation.cycle_count.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.cycle_count.CycleRepository
import com.example.jaywarehouse.data.cycle_count.models.CycleDetailRow
import com.example.jaywarehouse.data.cycle_count.models.CycleRow
import com.example.jaywarehouse.data.loading.LoadingRepository
import com.example.jaywarehouse.data.loading.models.LoadingListGroupedRow
import com.example.jaywarehouse.data.pallet.model.PalletConfirmRow
import com.example.jaywarehouse.data.transfer.TransferRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.cycle_count.contracts.CycleDetailContract
import com.example.jaywarehouse.presentation.loading.contracts.LoadingDetailContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CycleDetailViewModel(
    private val repository: CycleRepository,
    private val transferRepo: TransferRepository,
    private val prefs: Prefs,
    private val row: CycleRow,
) : BaseViewModel<CycleDetailContract.Event,CycleDetailContract.State,CycleDetailContract.Effect>(){
    init {
        val selectedSort = state.sortList.find {
            it.sort == prefs.getCycleDetailSort() && it.order == Order.getFromValue(prefs.getCycleDetailOrder())
        }
        if (selectedSort!=null) {
            setState {
                copy(sort = selectedSort)
            }
        }
        setState {
            copy(
                cycleRow = row,
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
        getDetails()
    }

    override fun setInitState(): CycleDetailContract.State {
        return CycleDetailContract.State()
    }

    override fun onEvent(event: CycleDetailContract.Event) {
        when(event){
            CycleDetailContract.Event.OnNavBack -> {
                setEffect {
                    CycleDetailContract.Effect.NavBack
                }
            }
            CycleDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }
            is CycleDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(
                        selectedCycle = event.detail,
                        quantity = TextFieldValue(),
                        quantityInPacket = TextFieldValue(),
                        batchNumber = TextFieldValue(),
                        expireDate = TextFieldValue()
                    )
                }
            }

            CycleDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            CycleDetailContract.Event.OnReachEnd -> {
                if (10*state.page<=state.details.size){
                    setState {
                        copy(page = state.page+1, loadingState = Loading.LOADING)
                    }
                    getDetails()
                }
            }

            CycleDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, details = emptyList(), loadingState = Loading.REFRESHING)
                }
                getDetails()
            }
            is CycleDetailContract.Event.OnSave -> {
                updateQuantity(event.item)
            }
            is CycleDetailContract.Event.OnChangeKeyword -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            CycleDetailContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, details = emptyList(), page = 1)
                }
                getDetails()
            }
            is CycleDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }
            is CycleDetailContract.Event.OnSortChange -> {
                prefs.setCycleDetailSort(event.sortItem.sort)
                prefs.setCycleDetailOrder(event.sortItem.order.value)
                setState {
                    copy(sort = event.sortItem, page = 1, details = emptyList(), loadingState = Loading.LOADING)
                }
                getDetails()
            }

            is CycleDetailContract.Event.OnShowAddDialog -> {
                if (event.show){
                    getStatusList()
                }
                setState {
                    copy(
                        showAddDialog = event.show,
                        locationCode = TextFieldValue(),
                        barcode = TextFieldValue(),
                        quantity = TextFieldValue(),
                        status = TextFieldValue(),
                        selectedStatus = null,
                        quantityInPacket = TextFieldValue(),
                        batchNumber = TextFieldValue(),
                        expireDate = TextFieldValue()
                    )
                }
            }

            CycleDetailContract.Event.OnAdd -> {}
            is CycleDetailContract.Event.OnChangeBarcode -> {
                setState {
                    copy(barcode = event.barcode)
                }
            }
            is CycleDetailContract.Event.OnChangeBatchNumber -> {
                setState {
                    copy(batchNumber = event.batchNumber)
                }
            }
            is CycleDetailContract.Event.OnChangeExpireDate -> {
                setState {
                    copy(expireDate = event.expireDate)
                }
            }
            is CycleDetailContract.Event.OnChangeLocationCode -> {
                setState {
                    copy(locationCode = event.locationCode)
                }
            }
            is CycleDetailContract.Event.OnChangeQuantity -> {
                setState {
                    copy(quantity = event.quantity)
                }
            }
            is CycleDetailContract.Event.OnChangeQuantityInPacket -> {
                setState {
                    copy(quantityInPacket = event.quantityInPacket)
                }
            }
            is CycleDetailContract.Event.OnShowDatePicker -> {
                setState {
                    copy(showDatePicker = event.show)
                }
            }

            is CycleDetailContract.Event.OnSelectStatus -> {
                setState {
                    copy(selectedStatus = event.status)
                }
            }
        }
    }


    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCycleCountLocationDetail(
                cycleCountWorkerTaskID = row.cycleCountWorkerTaskID,
                keyword = state.keyword.text,
                sort = state.sort.sort,
                page = state.page,
                order = state.sort.order.value
            )
                .catch {
                    setState {
                        copy(
                            error = it.message ?: "",
                            loadingState = Loading.NONE
                        )
                    }
                }
                .collect {
                    setSuspendedState {
                        copy(loadingState = Loading.NONE)
                    }
                    when(it){
                        is BaseResult.Success -> {
                            setState {
                                copy(
                                    details = details + (it.data?.rows ?: emptyList()),
                                )
                            }
                        }
                        is BaseResult.Error -> {
                            setState {
                                copy(
                                    error = it.message,
                                )
                            }
                        }
                        else -> {}
                    }
                }
        }
    }


    private fun add(){
        if (state.selectedStatus!=null) {
            setState { 
                copy(isAdding = true)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertTaskDetail(
                    state.barcode.text,
                    state.batchNumber.text,
                    state.expireDate.text,
                    state.selectedStatus!!.quiddityTypeId.toString(),
                    state.quantity.text.toInt()
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
                            setSuspendedState {
                                copy(showAddDialog = false)
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
            }
        }
    }

    private fun getStatusList() {

        viewModelScope.launch(Dispatchers.IO) {
            transferRepo.getProductStatuses()
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
                                copy(statusList = it.data?.rows?: emptyList())
                            }
                        }
                        BaseResult.UnAuthorized -> {}
                    }
                }
        }
    }

    private fun updateQuantity(item: CycleDetailRow){

    }
}