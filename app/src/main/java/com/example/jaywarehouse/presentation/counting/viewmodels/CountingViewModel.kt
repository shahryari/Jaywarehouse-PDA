package com.example.jaywarehouse.presentation.counting.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.example.jaywarehouse.data.common.utils.BaseResult
import com.example.jaywarehouse.data.common.utils.Prefs
import com.example.jaywarehouse.data.receiving.repository.ReceivingRepository
import com.example.jaywarehouse.presentation.common.utils.BaseViewModel
import com.example.jaywarehouse.presentation.common.utils.Loading
import com.example.jaywarehouse.presentation.common.utils.Order
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract
import com.example.jaywarehouse.presentation.counting.contracts.CountingContract.Effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CountingViewModel(
    private val repository: ReceivingRepository,
    private val prefs: Prefs
) : BaseViewModel<CountingContract.Event,CountingContract.State,CountingContract.Effect>(){

    override fun setInitState(): CountingContract.State {
        return CountingContract.State()
    }

    init {
        setState {
            copy(
                sort = prefs.getCountingSort(),
                order = prefs.getCountingOrder(),
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

    private fun getCountingList(keyword: String = "",page: Int = 1, order: String = Order.Desc.value,sort: String = "CreatedOn") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReceivingList(keyword,page,10,order,sort,)
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
                                copy(receivingModel = it.data,countingList = countingList+(it.data?.rows?: emptyList()))
                            }
                        }
                        else ->{}
                    }
                }
        }
    }


    override fun onEvent(event: CountingContract.Event) {
        when(event){
            is CountingContract.Event.OnKeywordChange -> {
                setState {
                    copy(keyword = event.keyword)
                }
            }
            is CountingContract.Event.OnNavToReceivingDetail -> setEffect {
                NavToReceivingDetail(event.receivingRow)
            }

            CountingContract.Event.ClearError -> {
                setState {
                    copy(error = "")
                }
            }

            is CountingContract.Event.OnSelectSort -> {
                prefs.setCountingSort(event.sort)
                setState {
                    copy(sort = event.sort, page = 1, countingList = emptyList(), loadingState = Loading.LOADING)
                }
                getCountingList(state.keyword.text,state.page,state.order,event.sort)
            }
            is CountingContract.Event.OnShowSortList -> {
                setState { copy(showSortList = event.show) }
            }

            is CountingContract.Event.OnSelectOrder -> {
                prefs.setCountingOrder(event.order)
                setState {
                    copy(order = event.order, page = 1, countingList = emptyList(), loadingState = Loading.LOADING)
                }
                getCountingList(state.keyword.text,state.page,event.order,state.sort)
            }

            CountingContract.Event.OnListEndReached -> {
                if(state.page*10 <= state.countingList.size){
                    setState {
                        copy(page = page+1, loadingState = Loading.LOADING)
                    }
                    getCountingList(state.keyword.text,state.page,state.order,state.sort)
                }
            }

            CountingContract.Event.OnRefresh -> {
                setState {
                    copy(loadingState = Loading.REFRESHING, page = 1, countingList = emptyList())
                }
                getCountingList(state.keyword.text,state.page,state.order,state.sort)
            }

            CountingContract.Event.OnSearch -> {
                setState {
                    copy(loadingState = Loading.SEARCHING, page = 1, countingList = emptyList())
                }
                getCountingList(state.keyword.text,state.page,state.order,state.sort)
            }

            CountingContract.Event.FetchData -> {
                setState {
                    copy(loadingState = Loading.LOADING, page = 1, countingList = emptyList(), keyword = TextFieldValue())
                }
                getCountingList(state.keyword.text,state.page,state.order,state.sort)
            }

            CountingContract.Event.OnBackPressed -> {
                setEffect {
                    CountingContract.Effect.NavBack
                }
            }
        }
    }
}