package com.linari.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.receiving.model.ReceivingDetailRow
import com.linari.data.receiving.model.ReceivingRow
import com.linari.data.receiving.repository.ReceivingRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.counting.contracts.CountingDetailContract
import com.linari.presentation.counting.contracts.CountingDetailContract.Effect.OnNavToInception
import com.linari.presentation.counting.contracts.CountingInceptionContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingDetailViewModel(
    private val repository: ReceivingRepository,
    private val prefs: Prefs,
    private val isCrossDock: Boolean =false,
    private val receivingRow: ReceivingRow
) : BaseViewModel<CountingDetailContract.Event,CountingDetailContract.State,CountingDetailContract.Effect>() {
    init {
        val sort = state.sortList.find { it.sort == prefs.getCountingDetailSort() && it.order == Order.getFromValue(prefs.getCountingDetailOrder()) }
        if (sort != null) {
            setState {
                copy(sort = sort)
            }
        }
        setState {
            copy(countingRow = receivingRow)
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }
    override fun setInitState(): CountingDetailContract.State {
        return CountingDetailContract.State()
    }

    override fun onEvent(event: CountingDetailContract.Event) {
        when(event){


            CountingDetailContract.Event.OnNavBack -> setEffect {
                CountingDetailContract.Effect.NavBack
            }

            CountingDetailContract.Event.CloseError -> {
                setState {
                    copy(error = "")
                }
            }

            CountingDetailContract.Event.OnClearBarcode -> {
                setState {
                    copy(barcode = TextFieldValue(), showClearIcon = false)
                }
            }

//            is CountingDetailContract.Event.OnChangeKeyword -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }

            is CountingDetailContract.Event.OnSelectDetail -> {
                setState {
                    copy(selectedDetail = event.detail)
                }
            }

            CountingDetailContract.Event.HideToast -> {
                setState {
                    copy(toast = "")
                }
            }

            is CountingDetailContract.Event.OnSelectOrder -> {
                prefs.setCountingDetailOrder(event.order)
                setState {
                    copy(order = event.order, countingDetailRow = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
//                getReceivingDetailList(receivingRow.receivingID,state.keyword.text,state.page,state.sort,order = event.order)
            }
            is CountingDetailContract.Event.OnSelectSort -> {
                prefs.setCountingDetailSort(event.sort.sort)
                prefs.setCountingDetailOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, countingDetailRow = emptyList(), page = 1)
                }
                getReceivingDetailList()
            }
            is CountingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }


            CountingDetailContract.Event.OnReachedEnd -> {
                if (ROW_COUNT*state.page <= state.countingDetailRow.size){
                    setState {
                        copy(page = page+1)
                    }
                    getReceivingDetailList()
                }
            }

            is CountingDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, countingDetailRow = emptyList(), keyword = event.keyword)
                }
                getReceivingDetailList(Loading.SEARCHING)
            }

            CountingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, countingDetailRow = emptyList())
                }
                getReceivingDetailList(Loading.REFRESHING)
            }

            is CountingDetailContract.Event.OnDetailClick -> {
                setEffect {
                    OnNavToInception(event.detail)
                }
            }

            CountingDetailContract.Event.FetchData -> {
                setState {
                    copy(page = 1, countingDetailRow = emptyList())
                }
                getReceivingDetailList()
            }

            is CountingDetailContract.Event.OnConfirm -> {
                done(event.detail)
            }
        }
    }

    private fun getReceivingDetailList(
        loading: Loading = Loading.LOADING
    ) {
        if (state.loadingState == Loading.NONE){
            setState {
                copy(loadingState = loading)
            }
            viewModelScope.launch(Dispatchers.IO) {
                repository.getReceivingDetails(receivingRow.receivingID,isCrossDock,state.keyword,state.page,ROW_COUNT,sort = state.sort.sort,order = state.sort.order.value)
                    .catch {
                        setSuspendedState {
                            copy(error = it.message?:"", loadingState = Loading.NONE)
                        }
                    }
                    .collect {result->
                        setSuspendedState {
                            copy(loadingState = Loading.NONE)
                        }
                        when(result){
                            is BaseResult.Error -> {
                                setSuspendedState {
                                    copy(error = result.message)
                                }
                            }
                            is BaseResult.Success -> {

                                val list = state.countingDetailRow + (result.data?.rows?: emptyList())
                                setSuspendedState {
                                    copy(
                                        countingDetailModel = result.data,
                                        countingDetailRow = list,
                                        total = result.data?.receiving?.total ?: list.sumOf { it.quantity },
                                        scan = result.data?.receiving?.count ?:list.sumOf { it.countQuantity?:0.0 },
                                        rowCount = result.data?.total?:0,

                                    )
                                }
                                if (loading != Loading.SEARCHING && list.isEmpty()){
                                    setEffect {
                                        CountingDetailContract.Effect.NavBack
                                    }
                                }
                            }
                            else ->{}
                        }
                    }
            }
        }
    }

    fun done(detail: ReceivingDetailRow) {
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
                        copy(error = it.message?:"", isCompleting = false, selectedDetail = null)
                    }
                }.collect {
                    setSuspendedState {
                        copy(isCompleting = false, selectedDetail = null)
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
                                    copy(toast = it.data.messages.firstOrNull()?:"Finished successfully", page = 1, countingDetailRow = emptyList())
                                }
                                getReceivingDetailList()
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