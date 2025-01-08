package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.ORDER
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailRow
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanModel
import com.example.jaywarehouse.data.receiving.model.ReceivingDetailScanRemoveModel
import com.example.jaywarehouse.data.receiving.model.ReceivingRow
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.common.utils.SortItem
import com.example.jaywarehouse.presentation.counting.contracts.CountingDetailContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingDetailViewModel(
    private val repository: ReceivingRepository,
    private val prefs: Prefs,
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
        getReceivingDetailList(receivingRow.receivingID,"",sort = state.sort)
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
                    copy(sort = event.sort, countingDetailRow = emptyList(), page = 1, loadingState = Loading.LOADING)
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword,state.page,event.sort)
            }
            is CountingDetailContract.Event.OnShowSortList -> {
                setState {
                    copy(showSortList = event.show)
                }
            }


            CountingDetailContract.Event.OnReachedEnd -> {
                if (10*state.page <= state.countingDetailRow.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getReceivingDetailList(receivingRow.receivingID,state.keyword,state.page,state.sort)
                }
            }

            is CountingDetailContract.Event.OnSearch -> {
                setState {
                    copy(page = 1, countingDetailRow = emptyList(), loadingState = Loading.SEARCHING, keyword = event.keyword)
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword,state.page,state.sort)
            }

            CountingDetailContract.Event.OnRefresh -> {
                setState {
                    copy(page = 1, loadingState = Loading.REFRESHING, countingDetailRow = emptyList())
                }
                getReceivingDetailList(receivingRow.receivingID,state.keyword,state.page,state.sort)
            }

            is CountingDetailContract.Event.OnDetailClick -> {
                setEffect {
                    CountingDetailContract.Effect.OnNavToInception(event.detail)
                }
            }
        }
    }

    private fun getReceivingDetailList(receivingID: Int,keyword: String,page: Int = 1,sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingDetails(receivingID,keyword,page,10,sort = sort.sort,order = sort.order.value)
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
                                copy(countingDetailModel = it.data, countingDetailRow = it.data?.rows?: emptyList())
                            }
                        }
                        else ->{}
                    }
                }
        }
    }
}