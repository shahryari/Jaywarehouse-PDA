package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.common.utils.ROW_COUNT
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract.Effect.OnNavToInception
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
                    copy(selectedDetail = event.barcode)
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
                                    copy(
                                        countingDetailModel = it.data,
                                        countingDetailRow = it.data?.rows?: emptyList(),
                                        total = it.data?.rows?.sumOf { it.quantity } ?:0.0,
                                        scan = it.data?.rows?.sumOf { it.countQuantity?:0.0 }?:0.0
                                    )
                                }
                                if (loading != Loading.SEARCHING && it.data?.rows.isNullOrEmpty()){
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
}