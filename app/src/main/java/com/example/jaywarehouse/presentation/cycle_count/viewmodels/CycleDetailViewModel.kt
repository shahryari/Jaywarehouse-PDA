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
                setState {
                    copy(
                        showAddDialog = event.show,
                        locationCode = TextFieldValue(),
                        barcode = TextFieldValue(),
                        quantity = TextFieldValue(),
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
        }
    }


    private fun getDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStockTakingWorkerTaskList(
                stockTakingId = row.stockTakingID,
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


    private fun add(){}

    private fun updateQuantity(item: CycleDetailRow){
        val quantity = state.quantity.text.toIntOrNull()?:0
        val quantityInPacket = state.quantityInPacket.text.toIntOrNull() ?: 0
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuantity(
                quantity = quantity,
                quantityInPacket = quantityInPacket,
                expireDate = state.expireDate.text,
                stockTakingWorkerTaskID = item.stockTakingWorkerTaskId
            ).catch {
                setSuspendedState {
                    copy(
                        error = it.message?:""
                    )
                }
            }.collect {
                when(it){
                    is BaseResult.Error -> {
                        setSuspendedState {
                            copy(
                                error = it.message
                            )
                        }
                    }
                    is BaseResult.Success -> {
                        setSuspendedState {
                            copy(
                                selectedCycle = null,
                                loadingState = Loading.LOADING,
                                page = 1,
                                details = emptyList()
                            )
                        }
                        getDetails()
                    }
                    BaseResult.UnAuthorized -> TODO()
                }
            }
        }
    }
}