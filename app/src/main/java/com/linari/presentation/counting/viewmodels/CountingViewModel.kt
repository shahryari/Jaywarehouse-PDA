package com.linari.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.linari.data.common.utils.BaseResult
import com.linari.data.common.utils.Prefs
import com.linari.data.common.utils.ROW_COUNT
import com.linari.data.receiving.repository.ReceivingRepository
import com.linari.presentation.common.utils.BaseViewModel
import com.linari.presentation.common.utils.Loading
import com.linari.presentation.common.utils.Order
import com.linari.presentation.common.utils.SortItem
import com.linari.presentation.counting.contracts.CountingContract
import com.linari.presentation.counting.contracts.CountingContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingViewModel(
    private val repository: ReceivingRepository,
    private val isCrossDock: Boolean = false,
    private val prefs: Prefs
) : BaseViewModel<CountingContract.Event,CountingContract.State,CountingContract.Effect>(){

    override fun setInitState(): CountingContract.State {
        return CountingContract.State()
    }

    init {
        val sort = state.sortList.find {
            it.sort == prefs.getCountingSort() && it.order == Order.getFromValue(prefs.getCountingOrder())
        }
        if (sort!=null)setState {
            copy(
                sort = sort,
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getLockKeyboard().collect {
                setSuspendedState {
                    copy(lockKeyboard = it)
                }
            }
        }
    }

    private fun getCountingList(keyword: String = "",page: Int = 1, sort: SortItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingList(keyword,isCrossDock, warehouseID = prefs.getWarehouse()!!.id,page,ROW_COUNT,order = sort.order.value,sort = sort.sort)
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
                                    receivingModel = it.data,countingList = countingList+(it.data?.rows?: emptyList()),
                                    rowCount = it.data?.total?:0
                                )
                            }

                        }
                        else ->{}
                    }
                }
        }
    }


    override fun onEvent(event: CountingContract.Event) {
        when(event){
//            is CountingContract.Event.OnKeywordChange -> {
//                setState {
//                    copy(keyword = event.keyword)
//                }
//            }
            is CountingContract.Event.OnNavToReceivingDetail -> setEffect {
                NavToReceivingDetail(event.receivingRow)
            }

            CountingContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }

            is CountingContract.Event.OnSelectSort -> {
                prefs.setCountingSort(event.sort.sort)
                prefs.setCountingOrder(event.sort.order.value)
                setState {
                    copy(sort = event.sort, page = 1, countingList = emptyList(), loadingState = Loading.LOADING)
                }
                getCountingList(state.keyword,state.page,event.sort)
            }
            is CountingContract.Event.OnShowSortList -> {
                setState { copy(showSortList = event.show) }
            }

            is CountingContract.Event.OnSelectOrder -> {
                prefs.setCountingOrder(event.order)
                setState {
                    copy(order = event.order, page = 1, countingList = emptyList(), loadingState = Loading.LOADING)
                }
//                getCountingList(state.keyword.text,state.page,event.order,stat)
            }

            CountingContract.Event.OnListEndReached -> {
                if(state.page*ROW_COUNT <= state.countingList.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getCountingList(state.keyword,state.page,state.sort)
                }
            }

            CountingContract.Event.OnRefresh -> {
                setState {
                    copy(loadingState = Loading.REFRESHING, page = 1, countingList = emptyList())
                }
                getCountingList(state.keyword,state.page,state.sort)
            }

            is CountingContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, page = 1, countingList = emptyList(), keyword = event.keyword)
                }
                getCountingList(state.keyword,state.page,state.sort)
            }

            CountingContract.Event.FetchData -> {
                setState {
                    copy(loadingState = Loading.LOADING, page = 1, countingList = emptyList(), keyword = "")
                }
                getCountingList(state.keyword,state.page,state.sort)
            }

            CountingContract.Event.OnBackPressed -> {
                setEffect {
                    CountingContract.Effect.NavBack
                }
            }
        }
    }
}